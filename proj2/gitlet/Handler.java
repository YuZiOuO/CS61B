package gitlet;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import static gitlet.Utils.*;

public class Handler {
    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    static Repository loadRepository() {
        return Repository.load(join(GITLET_DIR, Repository.REPO_FILENAME));
    }

    static void init() throws IOException {
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
        List<String> allFiles = plainFilenamesIn(CWD);
        if (allFiles != null && !allFiles.contains(name)) {
            System.out.println("File does not exist.");
        }
        repo.stagingArea.add(name);
        repo.dump();
    }

    static void commit(String message) throws IOException {
        Repository repo = loadRepository();
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
    }

    static void find(String message) {

    }

    static void status() {
    }

    static void checkout(String arg1, String arg2, String arg3) {
        Repository repo = loadRepository();
        if (arg1.equals("--")) {
            if (repo.checkoutResetFile(arg2) == 1) {
                System.out.println("File does not exist in that commit.");
            }
        } else if (arg2.equals("--")) {
            int result = repo.checkoutCherryPickFile(arg1, arg3);
            switch (result) {
                case 1:
                    System.out.println("File does not exist in that commit.");
                    break;
                case 2:
                    System.out.println("No commit with that id exists.");
                    break;
            }
        } else {
            int result = repo.checkoutBranch(arg1);
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
