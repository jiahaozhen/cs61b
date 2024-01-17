package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.*;

public class GitInfo implements Serializable {
    private HashMap<String, String> branchMap;
    private String HEAD;
    private String currentBranchName;

    public GitInfo(String branchName, Commit initCommit) {
        HEAD = initCommit.generateID();
        branchMap = new HashMap<>(2);
        branchMap.put(branchName, HEAD);
        currentBranchName = branchName;
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
        this.HEAD = commit.generateID();
    }

    public void changeStatus(Commit commit) {
        changeHead(commit);
        branchMap.put(currentBranchName, HEAD);
    }

    public String getHEAD() {
        return HEAD;
    }

    public HashMap<String, String> getBranchInfo() {
        return branchMap;
    }

    public List<String> getBranchNames() {
        return (List<String>) branchMap.keySet();
    }

    public String getCurrentBranchName() {
        return currentBranchName;
    }

    public boolean haveBranch(String branchName) {
        if (branchMap.containsKey(branchName)) {
            return true;
        } else {
            return false;
        }
    }

    public void changeCurrentBranch(String BranchName) {
        currentBranchName = BranchName;
    }
}
