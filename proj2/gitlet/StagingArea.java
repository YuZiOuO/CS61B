package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.*;


/**
 * the abstract of CWD and stagingArea Files(Working Tree).
 */
class StagingArea implements Serializable {

    /**
     * The tree representing current staged files.
     */
    private Map<String, Blob> workTree;
    /**
     * The previous commit hash relative to the current work tree.
     */
    private String prevCommitHash;

    StagingArea() {
        prevCommitHash = null;
        workTree = new HashMap<>();
    }

    /**
     * Load the staging area.
     *
     * @return a StagingArea object.
     */
    static StagingArea load() {
        return readObject(join(Repository.GITLET_DIR, Config.STAGING_AREA_FILENAME)
                , StagingArea.class);
    }

    /**
     * Checkout to the given commit hash.
     *
     * @param commitHash the commit to checkout.
     */
    public void checkout(String commitHash) {
        Commit c = Commit.load(commitHash);
        checkout(c.hash, Commit.loadWorkTree(c.hash));
    }

    /**
     * Update the working Tree in the following steps:<br>
     * 1.if the file given by {@code name} is not null in the current working tree,
     * push the current version,and pop the working version if necessary.<br>
     * 2.if the file is null (i.e.staged for removal),just modify the working tree.
     *
     * @param name the file name to update.
     */
    void push(String name) {
        File src = join(Repository.CWD, name);
        Blob working = workTree.get(name);
        if (workTree.containsKey(name) && working == null) {
            Blob b = Blob.load(Commit.load(prevCommitHash).getFiles().get(name));
            workTree.put(name, b);
            b.ref();
        } else {
            workTree.put(name, Blob.push(src));
            if (working != null) {
                working.pop();
            }
        }
    }

    /**
     * Checkout to the given work tree,restore all files in the CWD.
     *
     * @param prevCommitHash the previous commit hash relative to current tree.
     * @param workTree       the tree of form <String,Blob> to checkout.
     */
    void checkout(String prevCommitHash, Map<String, Blob> workTree) {
        this.prevCommitHash = prevCommitHash;
        this.workTree = workTree;
        List<String> allFiles = plainFilenamesIn(Repository.CWD);
        if (allFiles != null) {
            for (String file : allFiles) {
                restrictedDelete(join(Repository.CWD, file));
            }
        }
        for (String file : workTree.keySet()) {
            restore(file, null);
        }
    }

    /**
     * Reset a file content to the staged or tracked version
     *
     * @param filename The file to reset.
     * @param blob     A blob object,representing content of the file.
     *                 If null is passed,restore the staged version.
     */
    void restore(String filename, Blob blob) {
        File dest = join(Repository.CWD, filename);
        blob = (blob == null) ? workTree.get(filename) : blob;
        if (blob != null) {
            writeContents(dest, blob.content);
        }
    }

    /**
     * Remove a file from the staging area.
     *
     * @param name the file to remove.
     */
    void remove(String name) {
        File file = join(Repository.CWD, name);
        Blob prev = Commit.load(prevCommitHash) == null
                ? null : Blob.load(Commit.load(prevCommitHash).getFiles().get(name));
        Blob working = workTree.get(name);
        if (prev == null) {
            // newly added
            workTree.remove(name);
        } else if (working.hash.equals(prev.hash)) {
            // tracked and not modified
            workTree.put(name, null);
            restrictedDelete(file);
        } else {
            // modified
            workTree.put(name, prev);
            prev.ref();
        }
        working.pop();
    }

    /**
     * Convert currently staged files to a commit.
     *
     * @param message   the commit message
     * @param parent    the commit parent to track
     * @param timestamp the commit time
     * @return a commit object
     */
    Commit toCommit(String message, String[] parent, Date timestamp) {
        // delete the file if staged for removal
        workTree.entrySet().removeIf(entry -> entry.getValue() == null);
        Commit c = new Commit(message, parent, timestamp, workTree);
        for (Blob f : workTree.values()) {
            f.ref();
        }
        prevCommitHash = c.hash;
        return c;
    }

    /**
     * Test if the given the file is tracked.
     *
     * @param filename the file to test
     * @return whether the file is in the staging area.
     */
    boolean contains(String filename) {
        return filename != null && workTree.containsKey(filename);
    }

    /**
     * @return A set of files that is staged,not include those staged for removal.
     */
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

    /**
     * Test if the given file is staged for removal.
     *
     * @param filename the file to test.
     * @return whether it is staged for removal.
     */
    boolean fileStagedForRemoval(String filename) {
        Commit prev = Commit.load(prevCommitHash);
        return !(prev == null)
                && prev.contain(filename) && workTree.get(filename) == null;
    }

    /**
     * @return Files that are staged for removal.
     */
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

    /**
     * @return Files that were removed manually and not logged by the rm command.
     */
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

    /**
     * @return Files that were modified and unstaged.
     */
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

    /**
     * @return A set of names of files that is untracked ,
     * i.e. neither newly staged nor tracked by the previous commit.
     */
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

    /**
     * Dump this StagingArea object to file and save.
     */
    void dump() {
        writeObject(join(Repository.GITLET_DIR, Config.STAGING_AREA_FILENAME), this);
    }
}
