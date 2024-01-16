package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.writeObject;

public class RemoveFile implements Serializable {
    private HashSet<String> rmFileList;

    public RemoveFile() {
        rmFileList = null;
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

    public HashSet<String> getFileList() {
        return rmFileList;
    }
}
