package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.*;

// the abstract of CWD and stagingArea Files.
class StagingArea implements Serializable {
    public static final String STAGING_AREA_FILENAME = Repository.STAGING_AREA_FILENAME;

    private String prevCommitHash;
    private Map<String, Blob> workTree;

    //for checkout
    public void checkout(String commitHash) {
        Commit c = Commit.load(commitHash);
        this.prevCommitHash = c.parent;
        this.workTree = Commit.loadWorkTree(c.hash);
        List<String> allFiles = plainFilenamesIn(Repository.CWD);
        if (allFiles != null) {
            for (String file : allFiles) {
                File f = join(Repository.CWD, file);
                f.delete();
            }
        }
        for (String file : workTree.keySet()) {
            restore(file, null);
        }
    }

    StagingArea() {
        prevCommitHash = null;
        workTree = new HashMap<>();
    }

    void add(String name) {
        File src = join(Repository.CWD, name);
        Blob working = workTree.get(name);
        workTree.put(name, Blob.push(src));
        if (working != null) {
            working.pop();
        }
    }

    void remove(String name) {
        File file = join(Repository.CWD, name);
        Blob prev = Commit.load(prevCommitHash).getBlob(name);
        Blob working = workTree.get(name);
        if (working.hash.equals(prev.hash)) {
            // tracked and not modified
            workTree.put(name, null);
            file.delete();
        } else {
            // modified
            workTree.put(name, prev);
            writeContents(file, prev.content);
            prev.ref();
        }
        working.pop();
    }

    //reset a file content to the staged or tracked version
    void restore(String filename, Blob blob) {
        File dest = join(Repository.CWD, filename);
        blob = (blob == null) ? workTree.get(filename) : blob;
        writeContents(dest, blob.content);
    }

    Commit toCommit(String message, String parent, Date timestamp) {
        for (String filename : workTree.keySet()) {
            // delete the file if staged for removal
            if (workTree.get(filename) == null) {
                workTree.remove(filename);
            }
        }
        Commit c = new Commit(message, parent, timestamp, workTree);
        for (Blob f : workTree.values()) {
            f.ref();
        }
        prevCommitHash = c.hash;
        return c;
    }

    static StagingArea load() {
        return readObject(join(Repository.GITLET_DIR, STAGING_AREA_FILENAME)
                , StagingArea.class);
    }

    void dump() {
        writeObject(join(Repository.GITLET_DIR, STAGING_AREA_FILENAME), this);
    }

    boolean contains(String filename) {
        return filename != null && workTree.containsKey(filename);
    }

    boolean fileStagedForRemoval(String filename) {
        Commit prev = Commit.load(prevCommitHash);
        return !(prev == null)
                && prev.contain(filename) && workTree.get(filename) == null;
    }

    //files staged,not include those staged for removal.
    Set<String> filesStaged() {
        HashSet<String> staged = new HashSet<>();
        Map<String, String> prev =
                (prevCommitHash == null) ? new HashMap<>() : Commit.load(prevCommitHash).getFiles();
        for (String filename : workTree.keySet()) {
            Blob blob = workTree.get(filename);
            if (blob != null && !blob.hash.equals(prev.get(filename))) {
                staged.add(filename);
            }
        }
        return staged;
    }

    Set<String> filesStagedForRemoval() {
        HashSet<String> stagedForRemoval = new HashSet<>();
        for (String filename : workTree.keySet()) {
            Blob blob = workTree.get(filename);
            if (blob == null) {
                stagedForRemoval.add(filename);
            }
        }
        return stagedForRemoval;
    }

    Set<String> filesUnstagedForRemoval() {
        HashSet<String> unstagedForRemoval = new HashSet<>();
        for (String filename : workTree.keySet()) {
            Blob blob = workTree.get(filename);
            if (blob != null && !join(Repository.CWD, filename).exists()) {
                unstagedForRemoval.add(filename);
            }
        }
        return unstagedForRemoval;
    }

    Set<String> filesUnstagedForModification() {
        HashSet<String> modified = new HashSet<>();
        for (String filename : workTree.keySet()) {
            Blob blob = workTree.get(filename);
            if (blob != null) {
                File f = join(Repository.CWD, filename);
                if (f.exists() && !blob.hash.equals(sha1(readContents(f)))) {
                    modified.add(filename);
                }
            }
        }
        return modified;
    }

    Set<String> filesUntracked() {
        HashSet<String> untracked = new HashSet<>();
        List<String> allFiles = plainFilenamesIn(Repository.CWD);
        if (allFiles != null) {
            for (String file : allFiles) {
                if (!workTree.containsKey(file)) {
                    untracked.add(file);
                }
            }
        }
        return untracked;
    }
}
