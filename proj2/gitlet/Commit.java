package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.*;

import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Jia Haozhen
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
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

    /* TODO: fill in the rest of this class. */
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
}
