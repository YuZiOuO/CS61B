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
        if(staging.fileStagedForRemoval(name)){
            staging.restore(name,null);
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
        if(message.isEmpty()){
            System.out.println("Please enter a commit message.");
            return;
        }
        Repository repo = loadRepository();
        if (repo.stagingArea.filesStaged().isEmpty()
                && repo.stagingArea.filesStagedForRemoval().isEmpty()) {
            System.out.println("No changes added to the commit.");
            return;
        }
        repo.commit(message, Date.from(Instant.now()));
        repo.dump();
    }

    static void rm(String name) {
        Repository repo = loadRepository();
        StagingArea stagingArea = repo.stagingArea;
        if (!stagingArea.contains(name)) {
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

    static void checkoutBranch(String branch){
        Repository repo = loadRepository();
        if(repo.getCurrentBranch().equals(branch)){
            System.out.println("No need to checkout the current branch.");
        }else if(repo.getReference(branch) == null){
            System.out.println("No such branch exists.");
        }else{
            repo.checkoutBranch(branch);
        }
        repo.dump();
    }

    static void checkoutCherryPick(String name,String commit){
        Repository repo = loadRepository();
        if(commit != null){
            String completeHash = repo.getCommitHash(commit);
            if(completeHash == null){
                System.out.println("No commit with that id exists.");
            }else if(Commit.load(completeHash).getFiles().get(name) == null){
                System.out.println("File does not exist in that commit.");
            }else{
                repo.checkoutCherryPickFile(name,completeHash);
            }
        }else{
            repo.checkoutCherryPickFile(name,null);
        }
        repo.dump();
    }

    static void branch(String name) {
        Repository repo = loadRepository();
        if(repo.getReference(name) != null){
            System.out.println("A branch with that name already exists.");
        }else{
            repo.createBranch(name);
        }
        repo.dump();
    }

    static void rmBranch(String name) {
        Repository repo = loadRepository();
        if(repo.getReference(name) == null){
            System.out.println("A branch with that name does not exist.");
        }else if(name.equals(repo.getCurrentBranch())){
            System.out.println("Cannot remove the current branch.");
        }else{
            repo.removeBranch(name);
        }
        repo.dump();
    }

    static void reset(String hash) {
        Repository repo = loadRepository();
        hash = repo.getCommitHash(hash);
        if(hash == null){
            System.out.println("No commit with that id exists.");
        }else if(!repo.stagingArea.filesStaged().isEmpty()
                || !repo.stagingArea.filesStagedForRemoval().isEmpty()){
            System.out.println(
                    "There is an untracked file in the way; " +
                            "delete it, or add and commit it first.");
        }else{
            repo.reset(hash);
        }
        repo.dump();
    }

    static void merge(String name) {
    }
}
