package gitlet;

import java.io.File;
import java.time.Instant;
import java.util.Date;

import static gitlet.Utils.*;

public class Handler {
    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    static Repository loadRepository() {
        return Repository.load(join(GITLET_DIR, Repository.REPO_FILENAME));
    }

    static void init(){
        if (GITLET_DIR.mkdirs()) {
            Repository repo = new Repository();
            repo.dump();
        } else {
            System.out.println(
                    "A Gitlet version-control system already exists in the current directory.");
        }
    }

    static void add(String name) {
        Repository repo = loadRepository();
        StagingArea staging = repo.stagingArea;
        if(staging.stagedForRemoval(name)){
            staging.restore(name);
        }else{
            File f = join(CWD,name);
            if(!f.exists()){
                System.out.println("File does not exist.");
                return;
            }
        }
        staging.add(name);
        repo.dump();
    }

    static void commit(String message){
        Repository repo = loadRepository();
        if(repo.stagingArea.nothingToCommit()){
            System.out.println("No changes added to the commit.");
            return;
        }
        repo.commit(message, Date.from(Instant.now()));
        repo.dump();
    }

    static void rm(String name) {
        Repository repo = loadRepository();
        StagingArea stagingArea = repo.stagingArea;
        if (!stagingArea.contains(name) && !repo.HEAD().contain(name)) {
            System.out.println("No reason to remove the file.");
            return;
        }
        repo.stagingArea.remove(name);
        repo.dump();
    }

    static void log() {
        Repository repo = loadRepository();
        System.out.println(repo.log());
        repo.dump();
    }

    static void globalLog() {
        Repository repo = loadRepository();
        System.out.println(repo.globalLog());
        repo.dump();
    }

    static void find(String message) {
        Repository repo = loadRepository();
        System.out.println(repo.find(message));
        repo.dump();
    }

    static void status() {
    }

    static void checkoutbranch(String branch){
        Repository repo = loadRepository();
        int result = repo.checkoutBranch(branch);
        switch (result) {
            case 1:
                System.out.println("No such branch exists.");
                break;
            case 2:
                System.out.println(
                        "No need to checkout the current branch.");
                break;
            case 3:
                System.out.println(
                        "There is an untracked file in the way; " +
                                "delete it, or add and commit it first.");
                break;
        }
        repo.dump();
    }

    static void checkoutRestore(String name){
        Repository repo = loadRepository();
        if (repo.checkoutResetFile(name) == 1) {
            System.out.println("File does not exist in that commit.");
        }
        repo.dump();
    }

    static void checkoutCherryPick(String commit,String name){
        Repository repo = loadRepository();
        int result = repo.checkoutCherryPickFile(commit, name);
        switch (result) {
            case 1:
                System.out.println("File does not exist in that commit.");
                break;
            case 2:
                System.out.println("No commit with that id exists.");
                break;
        }
        repo.dump();
    }

    static void branch(String name) {
        Repository repo = loadRepository();
        if(repo.branchExists(name)){
            System.out.println("A branch with that name already exists.");
        }else{
            repo.createBranch(name);
        }
        repo.dump();
    }

    static void rmBranch(String name) {
        Repository repo = loadRepository();
        if(!repo.branchExists(name)){
            System.out.println("A branch with that name does not exist.");
        }else if(name.equals(repo.getCurrentBranch())){
            System.out.println("Cannot remove the current branch.");
        }else{
            repo.removeBranch(name);
        }
        repo.dump();
    }

    static void reset(String hash) {
    }

    static void merge(String name) {
    }
}
