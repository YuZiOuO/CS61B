package gitlet;

import java.io.File;
import java.io.IOException;
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
        repo.commit(message, now());
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
    }

    static void globalLog() {
    }

    static void find(String message) {

    }

    static void status() {
    }

    static void checkout(String arg1, String arg2, String arg3) {
        // TODO: args checking.
        Repository repo = loadRepository();
        if (arg1.equals("--")) {
            repo.checkoutResetFile(arg2);
        } else if (arg2.equals("--")) {
            repo.checkoutCherryPickFile(arg1, arg3);
        } else {
            //TODO
        }
        repo.dump();
    }

    static void branch(String name) {
    }

    static void rmBranch(String name) {
    }

    static void reset(String hash) {
    }

    static void merge(String name) {
    }
}
