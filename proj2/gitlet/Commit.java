package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  does at a high level.
 *
 *  @author Jia Haozhen
 */
public class Commit implements Serializable {
    /**
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    private Date timestamp;
    private String parent1;
    private String parent2;
    private HashMap<String, String> filenameToBlob;

    public Commit(String message, Commit parent1, Commit parent2) {
        this.message = message;
        timestamp = new Date();
        this.parent1 = sha1(parent1);
        this.parent2 = sha1(parent2);
        filenameToBlob = new HashMap<>();
    }

    public void setDate(Date date) {
        timestamp = date;
    }

    public void saveCommit() {
        File commitFile = join(Repository.COMMIT_DIR, sha1(this));
        try {
            commitFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        writeObject(commitFile, this);
    }

    public boolean haveSameFile(String blobSha1) {
        if (filenameToBlob.containsValue(blobSha1)) {
            return true;
        } else {
            return false;
        }
    }

    public void setFilenameToBlob(HashMap<String, String> FilenameToBlob) {
        this.filenameToBlob = FilenameToBlob;
    }

    public HashMap<String, String> getFilenameToBlob() {
        return filenameToBlob;
    }

    public void updateFile() {
        Blob stagedFileBlob = null;
        List<String> fileStaged = plainFilenamesIn(Repository.STAGING_DIR);
        for (String stagedFileName : fileStaged) {
            /* add the filename to the commit*/
            File stagedFile = join(Repository.STAGING_DIR, stagedFileName);
            stagedFileBlob = readObject(stagedFile, Blob.class);
            filenameToBlob.put(stagedFileBlob.getFileName(), stagedFileName);
            /* move the file in staging dir to file blobs*/
            restrictedDelete(stagedFile);
            stagedFileBlob.saveFile(Repository.BLOB_DIR);
        }
        /* delete the file that should be removed*/
        RemoveFile removedFileList = readObject(Repository.REMOVEDFILE, RemoveFile.class);
        for (String removedFile : removedFileList.getFileList()) {
            filenameToBlob.remove(removedFile);
        }
    }

    public void printLog() {
        printCommit();
        if (!this.parent1.equals(sha1((Object) null))) {
            Commit parent = Repository.getCommitFromSha1(parent1);
            parent.printCommit();
        }
    }

    void printCommit() {
        /* print the prompt */
        System.out.println("===");
        /* commit id */
        String id = sha1(this);
        System.out.println("commit " + id);
        /* merge information */
        if (!this.parent2.equals(sha1((Object) null))) {
            System.out.println("Merge: " + parent1.substring(0, 7) + " " + parent2.substring(0, 7));
        }
        /* the date */
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.CHINA);
        System.out.println(dateFormat.format(timestamp));
        /* commit message */
        System.out.println(this.message);
    }

    public boolean haveFile(String fileName) {
        return filenameToBlob.containsKey(fileName);
    }

    public String getMessage() {
        return message;
    }
}
