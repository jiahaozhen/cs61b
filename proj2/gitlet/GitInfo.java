package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.*;

public class GitInfo implements Serializable {
    private final Map<String, String> branchMap;
    private String HEAD;
    private String currentBranchName;
    public GitInfo(String branchName, Commit initCommit) {
        HEAD = initCommit.generateID();
        branchMap = new HashMap<>();
        branchMap.put(branchName, HEAD);
        currentBranchName = HEAD;
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

    public void addCommit(Commit commit) {
        String commitID = commit.generateID();
        HEAD = commitID;
        branchMap.put(getCurrentBranchName(), commitID);
    }

    public String getHEADID() {
        return HEAD;
    }

    public List<String> getBranchNames() {
        return (List<String>) branchMap.keySet();
    }

    public String getCurrentBranchName() {
        return currentBranchName;
    }

    public boolean haveBranch(String branchName) {
        return branchMap.containsKey(branchName);
    }

    public void createNewBranch(String branchName, Commit commit) {
        branchMap.put(branchName, commit.generateID());
    }

    public void removeBranch(String branchName) {
        branchMap.remove(branchName);
    }

    public Commit getHEADCommitOfBranch(String branchName) {
        String commitID = branchMap.get(branchName);
        return Repository.getCommitFromID(commitID);
    }

    public String getHEADOfBranch(String branchName) {
        return branchMap.get(branchName);
    }

    public void changeBranch(String branchName) {
        currentBranchName = branchName;
    }

    public void reset(Commit target) {
        HEAD = target.generateID();
        branchMap.put(currentBranchName, HEAD);
    }
}
