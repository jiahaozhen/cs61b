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
    private ArrayList<String> parents;
    private HashMap<String, String> filenameToBlob;

    public Commit(String message, Commit parent1, Commit parent2) {
        this.message = message;
        timestamp = new Date();
        parents = new ArrayList<>(2);
        if (parent1 != null) {
            parents.set(0, parent1.generateID());
        }
        if (parent2 != null) {
            parents.set(1, parent2.generateID());
        }
        filenameToBlob = new HashMap<>();
    }

    public void setDate(Date date) {
        timestamp = date;
    }

    public void saveCommit() {
        File commitFile = join(Repository.COMMIT_DIR, this.generateID());
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
        List<String> stagedFilesList = plainFilenamesIn(Repository.STAGING_DIR);
        for (String stagedFileName : stagedFilesList) {
            /* add the filename to the commit*/
            File stagedFile = join(Repository.STAGING_DIR, stagedFileName);
            stagedFileBlob = readObject(stagedFile, Blob.class);
            filenameToBlob.put(stagedFileBlob.getFileName(), stagedFileName);
            /* move the file in staging dir to file blobs*/
            restrictedDelete(stagedFile);
            stagedFileBlob.saveFile(Repository.BLOB_DIR);
        }
        /* delete the file that should be removed*/
        RemovedFile removedFiles = readObject(Repository.REMOVEDFILE, RemovedFile.class);
        for (String removedFile : removedFiles.getFileList()) {
            filenameToBlob.remove(removedFile);
        }
    }

    public void printLog() {
        printCommit();
        if (parents.get(0) != null) {
            Commit parent = Repository.getCommitFromSha1(parents.get(0));
            parent.printCommit();
        }
    }

    void printCommit() {
        /* print the prompt */
        System.out.println("===");
        /* commit id */
        String id = this.generateID();
        System.out.println("commit " + id);
        /* merge information */
        if (parents.get(1) != null) {
            System.out.println("Merge: " + parents.get(0).substring(0, 7) + " " + parents.get(1).substring(0, 7));
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

    public Set<String> getFileNames() {
        return filenameToBlob.keySet();
    }

    public String generateID() {
        return sha1(this.message, this.timestamp.toString(), this.parents.toString(), this.filenameToBlob.toString());
    }
}
