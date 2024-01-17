package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.writeObject;

public class RemovedFile implements Serializable {
    private List<String> rmFileList;

    public RemovedFile() {
        rmFileList = new ArrayList<>();
        saveFile();
    }

    public void saveFile() {
        File saveFile = Repository.REMOVEDFILE;
        try {
            saveFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
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
}
