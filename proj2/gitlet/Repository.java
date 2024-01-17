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
        gitInfo.changeStatus(newCommit);
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
        Commit currentCommit = getCommitFromSha1(ID);
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
        /* branch name exist? */
        gitInfo = readObject(GIT_INFO, GitInfo.class);
        if (!gitInfo.haveBranch(branchName)) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        /* already in the same branch? */
        if (gitInfo.getCurrentBranchName().equals(branchName)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        /* get commit */
        String commitID = gitInfo.getBranchInfo().get(branchName);
        Commit branchCommit = getCommitFromSha1(commitID);
        Commit currentCommit = getCurrentCommit();
        Set<String> trackedFileNames = currentCommit.getFileNames();
        /* untracked file */
        List<String> workingFiles = plainFilenamesIn(CWD);
        for (String fileName : workingFiles) {
            if (!trackedFileNames.contains(fileName)) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
        }
        /* delete file that are not in checkout branch */
        Set<String> branchFileNames = branchCommit.getFileNames();
        for (String fileName : workingFiles) {
            if (!branchFileNames.contains(fileName)) {
                restrictedDelete(join(CWD, fileName));
            }
        }
        /* overwrite/add file in checkout branch */
        for (String fileName : branchFileNames) {
            writeFileToCWD(fileName, branchCommit);
        }
        /* clear the staging area */
        List<String> stagedFilesList = plainFilenamesIn(Repository.STAGING_DIR);
        for (String stagedFileName : stagedFilesList) {
            File stagedFile = join(Repository.STAGING_DIR, stagedFileName);
            stagedFile.delete();
        }
        /* change status */
        gitInfo.changeHead(branchCommit);
        gitInfo.changeCurrentBranch(branchName);
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
        return getCommitFromSha1(HEAD);
    }

    static Commit getCommitFromSha1(String commitID) {
        List<String> allCommits = plainFilenamesIn(Repository.COMMIT_DIR);
        for (String commitName : allCommits) {
            if (commitName.equals(commitID)) {
                return readObject(join(Repository.COMMIT_DIR, commitName), Commit.class);
            }
        }
        return null;
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
}
