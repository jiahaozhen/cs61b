package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.*;

public class GitInfo implements Serializable {
    private ArrayList<String> branchNames;
    private ArrayList<String> branchCommits;
    private String HEAD;
    private int currentBranchIndex;

    public GitInfo(String branchName, Commit initCommit) {
        branchNames = new ArrayList<>(2);
        branchNames.add(0, branchName);
        branchCommits = new ArrayList<>(2);
        branchCommits.add(0, sha1(initCommit));
        HEAD = sha1(initCommit);
        currentBranchIndex = 0;
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
        branchCommits.set(currentBranchIndex, HEAD);
    }

    public String getHEAD() {
        return HEAD;
    }

    public ArrayList<String> getBranchNames() {
        return branchNames;
    }

    public int getCurrentBranchIndex() {
        return currentBranchIndex;
    }
}
