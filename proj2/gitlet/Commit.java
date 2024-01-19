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
    private final String message;
    private Date timestamp;
    private final ArrayList<String> parents;
    private Map<String, String> filenameToBlob;

    public Commit(String message, Commit parent1, Commit parent2) {
        this.message = message;
        timestamp = new Date();
        parents = new ArrayList<>(2);
        if (parent1 != null) {
            parents.add(0, parent1.generateID());
        } else {
            parents.add(0, null);
        }
        if (parent2 != null) {
            parents.set(1, parent2.generateID());
        } else {
            parents.add(1, null);
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

    boolean haveSameBlob(String blobSha1) {
        return filenameToBlob.containsValue(blobSha1);
    }

    public void setFilenameToBlob(Map<String, String> filenameToBlob) {
        this.filenameToBlob = filenameToBlob;
    }

    public Map<String, String> getFilenameToBlob() {
        return filenameToBlob;
    }

    public void updateFile() {
        Blob stagedFileBlob;
        /* have the file in the staging area */
        List<String> stagedFilesList = plainFilenamesIn(Repository.STAGING_DIR);
        assert stagedFilesList != null;
        for (String stagedFileName : stagedFilesList) {
            /* add the filename to the commit */
            File stagedFile = join(Repository.STAGING_DIR, stagedFileName);
            stagedFileBlob = readObject(stagedFile, Blob.class);
            filenameToBlob.put(stagedFileBlob.getFileName(), stagedFileName);
            /* move the file in staging dir to file blobs*/
            stagedFile.delete();
            stagedFileBlob.saveFile(Repository.BLOB_DIR);
        }
        /* delete the file that should be removed */
        RemovedFile removedFiles = readObject(Repository.REMOVEDFILE, RemovedFile.class);
        for (String removedFile : removedFiles.getFileList()) {
            filenameToBlob.remove(removedFile);
        }
        removedFiles.clear();
        removedFiles.saveFile();
    }

    public void printLog() {
        printCommit();
        if (parents.get(0) != null) {
            Commit parent = Repository.getCommitFromID(parents.get(0));
            assert  parent != null;
            parent.printLog();
        }
    }

    public void printCommit() {
        /* print the prompt */
        System.out.println("===");
        /* commit id */
        String id = this.generateID();
        System.out.println("commit " + id);
        /* merge information */
        if (parents.get(1) != null) {
            System.out.println("Merge: " + parents.get(0).substring(0, 7)
                    + " " + parents.get(1).substring(0, 7));
        }
        /* the date (copy it from GitHub) */
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.ENGLISH);
        System.out.println("Date: " + dateFormat.format(timestamp));
        /* commit message */
        System.out.println(this.message);
        System.out.println();
    }

    public boolean haveFile(String fileName) {
        return filenameToBlob.containsKey(fileName);
    }

    public boolean haveBlob(String blobID) {
        return filenameToBlob.containsValue(blobID);
    }

    public String getMessage() {
        return message;
    }

    public Set<String> getFileNames() {
        return filenameToBlob.keySet();
    }

    public String generateID() {
        return sha1(this.message, this.timestamp.toString(),
                this.parents.toString(), this.filenameToBlob.toString());
    }

    public Commit getParentCommit() {
        String parentCommitID = parents.get(0);
        if (parentCommitID != null) {
            return Repository.getCommitFromID(parentCommitID);
        } else {
            return null;
        }
    }

    public boolean haveParent() {
        return parents.get(0) != null;
    }

    public String getBlobIDofFile(String fileName) {
        return filenameToBlob.getOrDefault(fileName, null);
    }
}
