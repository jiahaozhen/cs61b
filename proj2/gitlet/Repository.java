package gitlet;

import java.io.File;
import java.util.*;

import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *  does at a high level.
 *
 *  @author Jia Haozhen
 */
public class Repository {
    /**
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The commit directory. */
    public static final File COMMIT_DIR = join(GITLET_DIR, "commits");
    /** The blob directory. */
    public static final File BLOB_DIR = join(GITLET_DIR, "blobs");
    /** The git info file. */
    public static final File GIT_INFO = join(GITLET_DIR, "gitInfo");
    /** The staging area(directory) **/
    public static final File STAGING_DIR = join(GITLET_DIR, "staging");
    /** The removed files  **/
    public static final File REMOVEDFILE = join(GITLET_DIR, "removedFiles");
    private static GitInfo gitInfo;
    private static RemovedFile removedFiles;

    public static void init() {
        /* failure case */
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        /* make directory */
        GITLET_DIR.mkdir();
        COMMIT_DIR.mkdir();
        BLOB_DIR.mkdir();
        STAGING_DIR.mkdir();
        removedFiles = new RemovedFile();
        /* create the initial commit */
        Commit initCommit = new Commit("initial commit", null, null);
        initCommit.setDate(new Date(0));
        initCommit.saveCommit();
        gitInfo = new GitInfo("master", initCommit);
        gitInfo.saveGitInfo();
    }

    public static void add(String fileName) {
        checkGitletExist();
        File addFile = join(CWD, fileName);
        /* failure case : file does not exist */
        if (!addFile.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        /* remove the file in removedFileList */
        removeFileInRemovedFiles(fileName);
        /* stage the file */
        Blob stagingBlob = new Blob(fileName);
        stagingBlob.stageFile();
    }

    public static void commit(String message) {
        checkGitletExist();
        /* failure case: message should not be empty */
        if (message.isBlank()) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }
        /* failure case: no file to commit */
        if (plainFilenamesIn(STAGING_DIR) == null) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        /* default setting */
        Commit newCommit = new Commit(message, getCurrentCommit(), null);
        newCommit.setFilenameToBlob(getCurrentCommit().getFilenameToBlob());
        /* commit the staged file */
        newCommit.updateFile();
        newCommit.saveCommit();
        /* change the git info */
        gitInfo = readObject(GIT_INFO, GitInfo.class);
        gitInfo.addCommit(newCommit);
        gitInfo.saveGitInfo();
    }

    public static void rm(String fileName) {
        checkGitletExist();
        /* if file is staged */
        Blob stagedFileBlob;
        boolean alreadyStaged = false;
        List<String> stagedFiles = plainFilenamesIn(Repository.STAGING_DIR);
        for (String stagedFileName : stagedFiles) {
            File stagedFile = join(Repository.STAGING_DIR, stagedFileName);
            stagedFileBlob = readObject(stagedFile, Blob.class);
            if (stagedFileBlob.getFileName().equals(fileName)) {
                stagedFile.delete();
                alreadyStaged = true;
                break;
            }
        }
        Commit currentCommit = getCurrentCommit();
        /* failure case */
        if (!alreadyStaged && !currentCommit.haveFile(fileName)) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
        /* if the file is in the current commit */
        if (currentCommit.haveFile(fileName)) {
            /* stage it for removal store the filename in removedFiles*/
            removedFiles = readObject(REMOVEDFILE, RemovedFile.class);
            removedFiles.addFile(fileName);
            removedFiles.saveFile();
            /* delete from working directory */
            restrictedDelete(join(CWD, fileName));
        }
    }

    public static void log() {
        checkGitletExist();
        Commit currentCommit = getCurrentCommit();
        currentCommit.printLog();
    }

    public static void globalLog() {
        checkGitletExist();
        List<String> allCommitName = plainFilenamesIn(Repository.COMMIT_DIR);
        for (String commitName : allCommitName) {
            File currentCommitFile = join(COMMIT_DIR, commitName);
            Commit currentCommit = readObject(currentCommitFile, Commit.class);
            currentCommit.printCommit();
        }
    }

    public static void find(String message) {
        checkGitletExist();
        boolean commitExist = false;
        List<String> allCommitName = plainFilenamesIn(Repository.COMMIT_DIR);
        for (String commitName : allCommitName) {
            File currentCommitFile = join(COMMIT_DIR, commitName);
            Commit currentCommit = readObject(currentCommitFile, Commit.class);
            if (currentCommit.getMessage().equals(message)) {
                currentCommit.printCommit();
                commitExist = true;
            }
        }
        if (!commitExist) {
            System.out.println("Found no commit with that message.");
        }
    }

    public static void status() {
        checkGitletExist();
        gitInfo = readObject(GIT_INFO, GitInfo.class);
        /* print the Branch information */
        System.out.println("=== Branches ===");
        List<String> branchNameList = gitInfo.getBranchNames();
        branchNameList.sort(Comparator.naturalOrder());
        for (String branchName : branchNameList) {
            if (branchName.equals(gitInfo.getCurrentBranchName())) {
                System.out.print("*");
            }
            System.out.println(branchName);
        }
        System.out.println();
        /* print the Staged Files */
        System.out.println("=== Staged Files ===");
        Blob stagedFileBlob;
        List<String> stagedFilesList = plainFilenamesIn(Repository.STAGING_DIR);
        if (stagedFilesList != null) {
            /* sort the filenames*/
            stagedFilesList.sort(Comparator.naturalOrder());
            for (String stagedFileName : stagedFilesList) {
                File stagedFile = join(Repository.STAGING_DIR, stagedFileName);
                stagedFileBlob = readObject(stagedFile, Blob.class);
                System.out.println(stagedFileBlob.getFileName());
            }
        }
        System.out.println();
        /* print the Removed Files */
        System.out.println("=== Removed Files ===");
        RemovedFile removedFiles = readObject(Repository.REMOVEDFILE, RemovedFile.class);
        List<String> removedFileList = removedFiles.getFileList();
        if (removedFileList != null) {
            /* sort the filenames*/
            removedFileList.sort(Comparator.naturalOrder());
            for (String removedFile : removedFiles.getFileList()) {
                System.out.println(removedFile);
            }
        }
        System.out.println();
        /* print the Modifications Not Staged */
        System.out.println("=== Modifications Not Staged For Commit ===");

        System.out.println();
        /* print the Untracked Files */
        System.out.println("=== Untracked Files ===");

        System.out.println();
    }

    public static void checkoutFile(String ID, String fileName) {
        checkGitletExist();
        /* get commit */
        Commit currentCommit = getCommitFromID(ID);
        if (currentCommit == null) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        writeFileToCWD(fileName, currentCommit);
    }

    public static void checkoutFile(String fileName) {
        checkGitletExist();
        /* get the HEAD commit */
        gitInfo = readObject(GIT_INFO, GitInfo.class);
        Commit currentCommit = getCurrentCommit();
        writeFileToCWD(fileName, currentCommit);
    }

    public static void checkoutBranch(String branchName) {
        checkGitletExist();
        /* failure case: branch name does not exist */
        gitInfo = readObject(GIT_INFO, GitInfo.class);
        if (!gitInfo.haveBranch(branchName)) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        /* failure case: already in the same branch */
        if (gitInfo.getCurrentBranchName().equals(branchName)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        /* change to the commit */
        Commit branchCommit = gitInfo.getHEADOfBranch(branchName);
        changeToCommit(branchCommit);
        /* change status */
        gitInfo.changeHead(branchCommit);
        gitInfo.changeBranch(branchName);
        gitInfo.saveGitInfo();
    }

    public static void branch(String branchName) {
        checkGitletExist();
        /* failure case: branch already exist */
        gitInfo = readObject(GIT_INFO, GitInfo.class);
        if (gitInfo.haveBranch(branchName)) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        /* create the new branch */
        gitInfo.createNewBranch(branchName, getCurrentCommit());
        gitInfo.saveGitInfo();
    }

    public static void removeBranch(String branchName) {
        checkGitletExist();
        /* failure case: branch already exist */
        gitInfo = readObject(GIT_INFO, GitInfo.class);
        if (!gitInfo.haveBranch(branchName)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        /* failure case: try to remove the current branch */
        if (gitInfo.getCurrentBranchName().equals(branchName)) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        /* delete the branch */
        gitInfo.removeBranch(branchName);
        gitInfo.saveGitInfo();
    }

    public static void reset(String commitID) {
        checkGitletExist();
        Commit targetCommit = getCommitFromID(commitID);
        /* failure case: no such commit */
        if (targetCommit == null) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        /* change to the commit */
        changeToCommit(targetCommit);
        /* change the git info */
        gitInfo = readObject(GIT_INFO, GitInfo.class);
        gitInfo.changeHead(targetCommit);
        gitInfo.saveGitInfo();
    }

    private static void checkGitletExist() {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }

    static Commit getCurrentCommit() {
        GitInfo presentInfo = readObject(GIT_INFO, GitInfo.class);
        String HEAD = presentInfo.getHEAD();
        return getCommitFromID(HEAD);
    }

    static Commit getCommitFromID(String commitID) {
        if (commitID.length() == 40) {
            File commitFile = join(COMMIT_DIR, commitID);
            if (commitFile.exists()) {
                return readObject(commitFile, Commit.class);
            } else {
                return null;
            }
        }
        List<String> allCommits = plainFilenamesIn(Repository.COMMIT_DIR);
        if (allCommits != null) {
            for (String commitName : allCommits) {
                if (sameCommitID(commitID, commitName)) {
                    return readObject(join(Repository.COMMIT_DIR, commitName), Commit.class);
                }
            }
        }
        return null;
    }

    private static boolean sameCommitID(String src, String target) {
        if (src.length() >= 6 && src.length() <= 40) {
            return target.startsWith(src);
        }
        return false;
    }

    private static void writeFileToCWD(String fileName, Commit currentCommit){
        /* get blob name from commit */
        Map<String, String> fileNameToBlob = currentCommit.getFilenameToBlob();
        if (!fileNameToBlob.containsKey(fileName)) {/* failure case: file does not exist in the commit*/
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        String fileBlobName = fileNameToBlob.get(fileName);
        /* get the file from the blob */
        Blob fileBlob = readObject(join(BLOB_DIR, fileBlobName), Blob.class);
        /* write the file content as needed */
        File checkoutFile = join(CWD, fileBlob.getFileName());
        writeContents(checkoutFile, fileBlob.getFileContent());
    }

    private static void removeFileInRemovedFiles(String fileName) {
        removedFiles = readObject(REMOVEDFILE, RemovedFile.class);
        removedFiles.removeFile(fileName);
        removedFiles.saveFile();
    }

    private static void untrackedFileCheck(Commit newCommit) {
        Commit currentCommit = getCurrentCommit();
        Set<String> trackedFileNames = currentCommit.getFileNames();
        Set<String> newFileNames = newCommit.getFileNames();
        List<String> workingFiles = plainFilenamesIn(CWD);
        if (workingFiles != null) {
            for (String fileName : workingFiles) {
                /* a working file is untracked in the current branch and would be overwritten by the reset */
                if (!trackedFileNames.contains(fileName) && newFileNames.contains(fileName)) {
                    System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                    System.exit(0);
                }
            }
        }
    }

    private static void clearStagingArea() {
        List<String> stagedFileList = plainFilenamesIn(Repository.STAGING_DIR);
        if (stagedFileList != null) {
            for (String stagedFileName : stagedFileList) {
                File stagedFile = join(Repository.STAGING_DIR, stagedFileName);
                stagedFile.delete();
            }
        }
        removedFiles = readObject(REMOVEDFILE, RemovedFile.class);
        removedFiles.clear();
        removedFiles.saveFile();
    }

    private static void changeToCommit(Commit targetCommit) {
        /* failure case: untracked file would be overwritten */
        untrackedFileCheck(targetCommit);
        /* delete file that are not in target commit */
        List<String> workingFiles = plainFilenamesIn(CWD);
        Set<String> branchFileNames = targetCommit.getFileNames();
        for (String fileName : workingFiles) {
            if (!branchFileNames.contains(fileName)) {
                restrictedDelete(join(CWD, fileName));
            }
        }
        /* overwrite/add file in checkout branch */
        for (String fileName : branchFileNames) {
            writeFileToCWD(fileName, targetCommit);
        }
        /* clear the staging area */
        clearStagingArea();
    }
}
