package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import static gitlet.Utils.*;

public class GitInfo implements Serializable {
    public String branch1Name;
    public String branch2Name;
    public String branch1Commit;
    public String branch2Commit;
    public String HEAD;

    public GitInfo(String branch1Name, Commit initCommit) {
        this.branch1Name = branch1Name;
        this.branch2Name = null;
        this.branch1Commit = sha1(initCommit);
        this.branch2Commit = null;
        this.HEAD = branch1Commit;
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
}
