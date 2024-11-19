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

    static void init() {
        if (GITLET_DIR.mkdirs()) {
            Repository repo = new Repository();
            repo.dump();
        } else {
            message(
                    "A Gitlet version-control system already exists in the current directory.");
        }
    }

    static void add(String name) {
        Repository repo = loadRepository();
        StagingArea staging = repo.stagingArea;
        if (staging.fileStagedForRemoval(name)) {
            staging.restore(name, null);
        } else {
            File f = join(CWD, name);
            if (!f.exists()) {
                message("File does not exist.");
                return;
            }
        }
        staging.add(name);
        repo.dump();
    }

    static void commit(String message) {
        if (message.isEmpty()) {
            message("Please enter a commit message.");
            return;
        }
        Repository repo = loadRepository();
        if (repo.stagingArea.filesStaged().isEmpty()
                && repo.stagingArea.filesStagedForRemoval().isEmpty()) {
            message("No changes added to the commit.");
            return;
        }
        repo.commit(message, Date.from(Instant.now()));
        repo.dump();
    }

    static void rm(String name) {
        Repository repo = loadRepository();
        StagingArea stagingArea = repo.stagingArea;
        if (!stagingArea.contains(name)) {
            message("No reason to remove the file.");
            return;
        }
        repo.stagingArea.remove(name);
        repo.dump();
    }

    static void log() {
        Repository repo = loadRepository();
        message(repo.log());
        repo.dump();
    }

    static void globalLog() {
        Repository repo = loadRepository();
        message(repo.globalLog());
        repo.dump();
    }

    static void find(String message) {
        Repository repo = loadRepository();
        message(repo.find(message));
        repo.dump();
    }

    static void checkoutBranch(String branch) {
        Repository repo = loadRepository();
        if (repo.getCurrentBranch().equals(branch)) {
            message("No need to checkout the current branch.");
        } else if (repo.getReference(branch) == null) {
            message("No such branch exists.");
        } else {
            repo.checkoutBranch(branch);
        }
        repo.dump();
    }

    static void checkoutCherryPick(String name, String commit) {
        Repository repo = loadRepository();
        if (commit != null) {
            String completeHash = repo.getCommitHash(commit);
            if (completeHash == null) {
                message("No commit with that id exists.");
            } else if (Commit.load(completeHash).getFiles().get(name) == null) {
                message("File does not exist in that commit.");
            } else {
                repo.checkoutCherryPickFile(name, completeHash);
            }
        } else {
            repo.checkoutCherryPickFile(name, null);
        }
        repo.dump();
    }

    static void branch(String name) {
        Repository repo = loadRepository();
        if (repo.getReference(name) != null) {
            message("A branch with that name already exists.");
        } else {
            repo.createBranch(name);
        }
        repo.dump();
    }

    static void rmBranch(String name) {
        Repository repo = loadRepository();
        if (repo.getReference(name) == null) {
            message("A branch with that name does not exist.");
        } else if (name.equals(repo.getCurrentBranch())) {
            message("Cannot remove the current branch.");
        } else {
            repo.removeBranch(name);
        }
        repo.dump();
    }

    static void reset(String hash) {
        Repository repo = loadRepository();
        hash = repo.getCommitHash(hash);
        if (hash == null) {
            message("No commit with that id exists.");
        } else if (!repo.stagingArea.filesStaged().isEmpty()
                || !repo.stagingArea.filesStagedForRemoval().isEmpty()) {
            message(
                    "There is an untracked file in the way; " +
                            "delete it, or add and commit it first.");
        } else {
            repo.reset(hash);
        }
        repo.dump();
    }

    static void status() {
        Repository repo = loadRepository();
        message(repo.status());
        repo.dump();
    }

    static void merge(String branch){
        Repository repo = loadRepository();
        StagingArea stagingArea = repo.stagingArea;
        if(!stagingArea.filesStagedForRemoval().isEmpty() ||
                !stagingArea.filesStaged().isEmpty()){
            message("You have uncommitted changes.");
            return;
        }
        if(repo.getReference(branch) == null){
            message("A branch with that name does not exist.");
            return;
        }
        if(branch.equals(repo.getCurrentBranch())){
            message("Cannot merge a branch with itself.");
            return;
        }
        if(!stagingArea.filesUnstagedForModification().isEmpty() ||
                !stagingArea.filesUnstagedForRemoval().isEmpty() ||
                !stagingArea.filesUntracked().isEmpty()){
            message("There is an untracked file in the way; " +
                    "delete it, or add and commit it first.");
            return;
        }
        repo.merge(branch);
        repo.dump();
    }
}