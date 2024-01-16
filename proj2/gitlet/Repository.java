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
    public static final File STAGING_DIR = join(GITLET_DIR, "staging");
    public static final File REMOVEDFILE = join(GITLET_DIR, "removeFileList");
    private static GitInfo gitInfo;
    private static RemovedFile removedFiles;

    public static void init() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            return;
        }
        GITLET_DIR.mkdir();
        COMMIT_DIR.mkdir();
        BLOB_DIR.mkdir();
        STAGING_DIR.mkdir();
        removedFiles = new RemovedFile();
        Commit initCommit = new Commit("initial commit", null, null);
        initCommit.setDate(new Date(0));
        initCommit.saveCommit();
        gitInfo = new GitInfo("master", initCommit);
        gitInfo.saveGitInfo();
    }

    public static void add(String fileName) {
        checkGitletExist();
        File addFile = join(CWD, fileName);
        if (!addFile.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        /* remove the filename in removedFileList */
        removedFiles = readObject(REMOVEDFILE, RemovedFile.class);
        removedFiles.removeFile(fileName);
        removedFiles.saveFile();
        /* stage the file */
        Blob stagingBlob = new Blob(fileName);
        stagingBlob.stageFile();
    }

    public static void commit(String message) {
        checkGitletExist();
        /* message should not be empty*/
        if (message.isBlank()) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }
        /* no file to commit */
        if (plainFilenamesIn(STAGING_DIR) == null) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        /* default setting */
        Commit newCommit = new Commit(message, getCurrentCommit(), null);
        newCommit.setFilenameToBlob(getCurrentCommit().getFilenameToBlob());
        /* commit the staged file*/
        newCommit.updateFile();
        newCommit.saveCommit();
        /* change the HEAD */
        gitInfo = readObject(GIT_INFO, GitInfo.class);
        gitInfo.changeHead(newCommit);
        gitInfo.saveGitInfo();
    }

    /** Unstage the file if it is currently staged for addition.
     * If the file is tracked in the current commit, stage it for removal
     * and remove the file from the working directory
     * if the user has not already done so
     * (do not remove it unless it is tracked in the current commit).
     * */
    public static void rm(String fileName) {
        checkGitletExist();
        /* if it's staged */
        Blob stagedFileBlob;
        boolean alreadyStaged = false;
        List<String> fileStaged = plainFilenamesIn(Repository.STAGING_DIR);
        for (String stagedFileName : fileStaged) {
            File stagedFile = join(Repository.STAGING_DIR, stagedFileName);
            stagedFileBlob = readObject(stagedFile, Blob.class);
            if (stagedFileBlob.getFileName().equals(fileName)) {
                restrictedDelete(stagedFile);
                alreadyStaged = true;
                break;
            }
        }
        Commit currentCommit = getCurrentCommit();
        /* failure cases */
        if (!alreadyStaged && !currentCommit.haveFile(fileName)) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
        /* if it's in the current commit */
        if (currentCommit.haveFile(fileName)) {
            /* stage it for removal store the filename in removedFileList*/
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
        for (int i = 0; i < branchNameList.size(); i++) {
            if (i == gitInfo.getCurrentBranchIndex()) {
                System.out.println("*" + branchNameList.get(i));
            } else {
                System.out.println(branchNameList.get(i));
            }
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

    private static void checkGitletExist() {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }

    public static Commit getCurrentCommit() {
        GitInfo presentInfo = readObject(GIT_INFO, GitInfo.class);
        String HEAD = presentInfo.getHEAD();
        return getCommitFromSha1(HEAD);
    }

    static Commit getCommitFromSha1(String sha1Code) {
        List<String> allCommits = plainFilenamesIn(Repository.COMMIT_DIR);
        for (String commitName : allCommits) {
            if (commitName.equals(sha1Code)) {
                return readObject(join(Repository.COMMIT_DIR, commitName), Commit.class);
            }
        }
        return null;
    }


}
