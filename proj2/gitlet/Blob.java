package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.*;

public class Blob implements Serializable {
    private String fileName;
    private String fileContent;

    public Blob(String fileName) {
        this.fileName = fileName;
        File newFile = join(Repository.CWD, fileName);
        this.fileContent = readContentsAsString(newFile);
    }

    public String getFileName() {
        return fileName;
    }
    public String getFileContent() {
        return fileContent;
    }
    public void saveFile(File DIR) {
        File blobFile = join(DIR, sha1(this));
        try {
            blobFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        writeObject(blobFile, this);
    }
    public void stageFile() {
        HashMap<String, String> fileNameToSha1 = new HashMap<>();
        Blob stagedFileBlob;
        List<String> fileStaged = plainFilenamesIn(Repository.STAGING_DIR);
        for (String stagedFileName : fileStaged) {
            File stagedFile = join(Repository.STAGING_DIR, stagedFileName);
            stagedFileBlob = readObject(stagedFile, Blob.class);
            fileNameToSha1.put(stagedFileBlob.getFileName(), stagedFileName);
        }
        /* now we have a hashmap from the staged file true name to its blob name */
        if (fileNameToSha1.containsKey(fileName)) { /* if we have the file of the same name */
            /* delete the old one */
            restrictedDelete(join(Repository.STAGING_DIR, fileNameToSha1.get(fileName)));
        }
        /* check if the file is the same in the current commit */
        Commit currentCommit = Repository.getCurrentCommit();
        if (currentCommit.haveSameFile(sha1(this))) {
            return;/* don't do anything if true*/
        }
        /* create the new one in the staging directory */
        saveFile(Repository.STAGING_DIR);
    }
}
