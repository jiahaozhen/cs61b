package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.readObject;
import static gitlet.Utils.writeObject;

public class RemovedFile implements Serializable {
    private List<String> rmFileList;

    public RemovedFile() {
        rmFileList = new ArrayList<>();
        saveFile();
    }

    public void saveFile() {
        File saveFile = Repository.REMOVEDFILE;
        if (!saveFile.exists()) {
            try {
                saveFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        writeObject(saveFile, this);
    }

    public void addFile(String fileName) {
        rmFileList.add(fileName);
    }

    public void removeFile(String fileName) {
        rmFileList.remove(fileName);
    }

    public List<String> getFileList() {
        return rmFileList;
    }

    public void clear() {
        rmFileList = new ArrayList<>();
    }

    public boolean isEmpty() {
        return this.rmFileList.isEmpty();
    }

    public static boolean haveStagedRemovedFiles() {
        RemovedFile removedFile = readObject(Repository.REMOVEDFILE, RemovedFile.class);
        return !removedFile.isEmpty();
    }
}
