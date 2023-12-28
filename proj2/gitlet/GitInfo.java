package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;

import static gitlet.Utils.*;

public class GitInfo implements Serializable {
    private String branch1Name;
    private String branch2Name;
    private String branch1Commit;
    private String branch2Commit;
    private String HEAD;
    private HashSet<String> rmFileList;

    public GitInfo(String branch1Name, Commit initCommit) {
        this.branch1Name = branch1Name;
        this.branch2Name = null;
        this.branch1Commit = sha1(initCommit);
        this.branch2Commit = null;
        this.HEAD = branch1Commit;
        this.rmFileList = new HashSet<>();
    }

    public void saveGitInfo() {
        File saveFile = Repository.GIT_INFO;
        try {
            saveFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        writeObject(saveFile, this);
    }

    public void changeHead(Commit commit) {
        this.HEAD = sha1(commit);
    }

    public void addRmFile(String fileName) {
        rmFileList.add(fileName);
    }

    public void removaRmFile(String fileName) {
        rmFileList.remove(fileName);
    }

    public HashSet<String> getRmFileList() {
        return rmFileList;
    }

    public String getHEAD() {
        return HEAD;
    }
}
