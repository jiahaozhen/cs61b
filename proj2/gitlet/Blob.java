package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.*;

public class Blob implements Serializable {
    private final String fileName;
    private final String fileContent;

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

    public void saveFile(File dir) {
        File blobFile = join(dir, generateID());
        try {
            blobFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        writeObject(blobFile, this);
    }

    public void stageFile() {
        Map<String, String> fileNameToSha1 = new HashMap<>();
        Blob stagedBlob;
        /* generate a map from the staged file's true name to its blob name */
        List<String> fileStaged = plainFilenamesIn(Repository.STAGING_DIR);
        if (fileStaged != null) {
            for (String stagedFileName : fileStaged) {
                File stagedFile = join(Repository.STAGING_DIR, stagedFileName);
                stagedBlob = readObject(stagedFile, Blob.class);
                fileNameToSha1.put(stagedBlob.getFileName(), stagedFileName);
            }
        }
        if (fileNameToSha1.containsKey(fileName)) { /* if we have the file of the same name */
            /* delete the old one */
            File oldBlob = join(Repository.STAGING_DIR, fileNameToSha1.get(fileName));
            oldBlob.delete();
        }
        /* check if the file is the same in the current commit */
        Commit currentCommit = Repository.getCurrentCommit();
        if (currentCommit.haveSameBlob(generateID())) {
            return; /* don't do anything if true */
        }
        /* create the new one in the staging directory */
        saveFile(Repository.STAGING_DIR);
    }

    private String generateID() {
        return sha1(fileName, fileContent);
    }

    public static boolean haveStagedAddFiles() {
        List<String> fileStaged = plainFilenamesIn(Repository.STAGING_DIR);
        assert fileStaged != null;
        return !fileStaged.isEmpty();
    }
}
