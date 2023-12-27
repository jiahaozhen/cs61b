package gitlet;

import java.io.File;
import java.util.Date;
import java.util.List;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Jia Haozhen
 */
public class Repository {
    /**
     * TODO: add instance variables here.
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
    public static final File STAGING_AREA = join(GITLET_DIR, "staging");

    /* TODO: fill in the rest of this class. */
    public static void init() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            return;
        }
        GITLET_DIR.mkdir();
        COMMIT_DIR.mkdir();
        BLOB_DIR.mkdir();
        STAGING_AREA.mkdir();
        Commit initCommit = new Commit("initial commit", null, null);
        initCommit.setDate(new Date(0));
        initCommit.saveCommit();
        GitInfo gitInfo = new GitInfo("master", initCommit);
        gitInfo.saveGitInfo();
    }

    public static void add(String fileName) {
        checkGitletExist();
        File addFile = join(CWD, fileName);
        if (!addFile.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        Blob stagingBlob = new Blob(fileName);
        stagingBlob.stageFile();
    }

    private static void checkGitletExist() {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }

    public static Commit getCurrentCommit() {
        GitInfo presentInfo = readObject(GIT_INFO, GitInfo.class);
        String HEAD = presentInfo.HEAD;
        return getCommitFromSha1(HEAD);
    }

    private static Commit getCommitFromSha1(String sha1Code) {
        List<String> allCommits = plainFilenamesIn(Repository.COMMIT_DIR);
        for (String commitName : allCommits) {
            if (commitName.equals(sha1Code)) {
                return readObject(join(Repository.COMMIT_DIR, commitName), Commit.class);
            }
        }
        return null;
    }


}
