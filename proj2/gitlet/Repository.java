package gitlet;

import java.io.File;
import java.io.Serializable;
import java.time.Instant;
import java.util.*;

import static gitlet.Utils.*;

/**
 * Represents a gitlet repository.
 */
public class Repository implements Serializable {

    /**
     * The current working directory.
     */
    public static final File CWD = Handler.CWD;
    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = Handler.GITLET_DIR;

    private String currentBranch;

    //maps ref name to commit hash
    private final Map<String, String> refs;

    private final Set<String> commits;

    //cache before dump
    StagingArea stagingArea;

    public Repository() {
        refs = new TreeMap<>();
        currentBranch = "master";
        commits = new TreeSet<>();
        stagingArea = new StagingArea();
        if (Commit.COMMIT_DIR.mkdirs() && Blob.BLOB_DIR.mkdirs()) {
            commit(Config.INITIAL_COMMIT_MESSAGE, Date.from(Instant.EPOCH));
        }
    }

    public void commit(String message, Date timestamp) {
        commit(message, timestamp, new String[]{refs.get(currentBranch)});
    }

    private void commit(String message, Date timestamp, String[] parent) {
        Commit c = stagingArea.toCommit(message, parent, timestamp);
        refs.put(currentBranch, c.hash);
        commits.add(c.hash);
        c.dump();
    }

    static Repository load(File file) {
        Repository repo = readObject(file, Repository.class);
        repo.stagingArea = StagingArea.load();
        return repo;
    }

    void dump() {
        stagingArea.dump();
        stagingArea = null;
        writeObject(join(GITLET_DIR, Config.REPO_FILENAME), this);
    }

    /**
     * Cherry-pick the given file in given commit to CWD, overwrite the currently
     * existing one.
     *
     * @param commit The commit which the file to be extracted from.
     * @param name   The file name.
     */
    void checkoutCherryPickFile(String name, String commit) {
        stagingArea.restore(name, commit == null
                ? null : Blob.load(Commit.load(commit).getFiles().get(name)));
    }

    /**
     * Checkout to the given branch, clear all files in CWD and replace with corresponding files
     * contained in the last commit of the given branch.
     *
     * @param branch The branch to checkout.
     */
    void checkoutBranch(String branch) {
        currentBranch = branch;
        stagingArea.checkout(refs.get(branch));
    }

    void createBranch(String name) {
        refs.put(name, refs.get(currentBranch));
    }

    void removeBranch(String name) {
        refs.remove(name);
    }

    String getCurrentBranch() {
        return currentBranch;
    }

    String log() {
        StringBuilder sb = new StringBuilder();
        Commit c = Commit.load(refs.get(currentBranch));
        while (c != null) {
            sb.append(c.toLog());
            c = Commit.load(c.parent.get(0));
        }
        return sb.toString();
    }

    String globalLog() {
        List<String> commit = plainFilenamesIn(Commit.COMMIT_DIR);
        StringBuilder sb = new StringBuilder();
        if (commit != null) {
            for (String n : commit) {
                sb.append(Commit.load(n).toLog());
            }
        }
        return sb.toString();
    }

    /**
     * Search in all commits,return a string of hash that
     * all of its corresponding commits have a message
     * contain the substring {@code string}
     *
     * @param string the string to find.
     * @return string containing hash.
     */
    String find(String string) {
        StringBuilder sb = new StringBuilder();
        for (String commit : commits) {
            Commit c = Commit.load(commit);
            String msg = c.getMessage();
            if (msg.contains(string)) {
                sb.append(c.hash).append("\n");
            }
        }
        if (sb.toString().isEmpty()) {
            sb.append("Found no commit with that message.");
        }
        return sb.toString();
    }

    void reset(String commitHash) {
        refs.put(currentBranch, commitHash);
        stagingArea.checkout(commitHash);
    }

    /**
     * find if any hash of commits of this repository matches the given substring hash.
     * A naive implementation with asymptotic O(n) where n is the total amount of commits.
     * Should be invoked only at higher layer.
     *
     * @param source @NotNull the substring hash.
     * @return null if nothing matches,otherwise the corresponding hash.
     */
    String getCommitHash(String source) {
        for (String s : commits) {
            if (matchHash(source, s)) {
                return s;
            }
        }
        return null;
    }

    String getRef(String ref) {
        return refs.get(ref);
    }

    String status() {
        StringBuilder sb = new StringBuilder();

        sb.append("=== Branches ===\n")
                .append("*").append(currentBranch).append("\n");
        for (String ref : refs.keySet()) {
            if (!ref.equals(currentBranch)) {
                sb.append(ref).append("\n");
            }
        }

        sb.append("\n=== Staged Files ===\n");
        for (String f : stagingArea.filesStaged()) {
            sb.append(f).append("\n");
        }

        sb.append("\n=== Removed Files ===\n");
        for (String f : stagingArea.filesStagedForRemoval()) {
            sb.append(f).append("\n");
        }

        sb.append("\n=== Modifications Not Staged For Commit ===\n");
        for (String f : stagingArea.filesUnstagedForRemoval()) {
            sb.append(f).append(" (deleted)\n");
        }
        for (String f : stagingArea.filesUnstagedForModification()) {
            sb.append(f).append(" (modified)\n");
        }

        sb.append("\n=== Untracked Files ===\n");
        for (String f : stagingArea.filesUntracked()) {
            sb.append(f).append("\n");
        }

        return sb.toString();
    }

    void merge(String branch) {
        String splitPoint = Commit.splitPoint(refs.get(currentBranch), refs.get(branch));
        if (splitPoint.equals(refs.get(branch))) {
            message("Given branch is an ancestor of the current branch.");
            refs.put(branch, refs.get(currentBranch));
        } else if (splitPoint.equals(refs.get(currentBranch))) {
            message("Current branch fast-forwarded.");
            stagingArea.checkout(refs.get(branch));
            refs.put(currentBranch, refs.get(branch));
        } else {
            Map<String, String> splitTree = Commit.load(splitPoint).getFiles();
            Map<String, String> currentTree = Commit.load(refs.get(currentBranch)).getFiles();
            Map<String, String> givenTree = Commit.load(refs.get(branch)).getFiles();

            Map<String, String> newTree = mergeTree(splitTree, currentTree, givenTree);

            stagingArea.checkout(refs.get(currentBranch), Commit.loadWorkTree(newTree));
            String[] parents = new String[]{refs.get(currentBranch), refs.get(branch)};
            commit("Merged " + branch + " into " + currentBranch + ".",
                    Date.from(Instant.now()), parents);
            refs.put(branch, refs.get(currentBranch));
        }
    }

    private Map<String, String> mergeTree(Map<String, String> splitTree,
                                  Map<String, String> currentTree,
                                  Map<String, String> givenTree) {
        boolean encounterMergeConflict = false;
        Set<String> allFiles = new HashSet<>(Set.copyOf(currentTree.keySet()));
        allFiles.addAll(givenTree.keySet());

        for (String f : allFiles) {
            String split = splitTree.get(f);
            String given = givenTree.get(f);
            String current = currentTree.get(f);
            if (!stringEquals(given, current)) {
                if (stringEquals(split, current)) {
                    currentTree.put(f, given);
                } else if (!stringEquals(split, given) && !stringEquals(split, current)) {
                    encounterMergeConflict = true;
                    Blob givenBlob = Blob.load(given);
                    Blob currentBlob = Blob.load(current);
                    String s = "<<<<<<< HEAD\n"
                            + (currentBlob == null ? "" : new String(currentBlob.content))
                            + "=======\n"
                            + (givenBlob == null ? "" : new String(givenBlob.content))
                            + ">>>>>>>\n";
                    Blob conflict = Blob.push(s.getBytes());
                    currentTree.put(f, conflict.hash);
                }
            }
        }
        if (encounterMergeConflict) {
            message("Encountered a merge conflict.");
        }
        return currentTree;
    }

    /**
     * Test if string a and b has the same content,including the case that
     * part of a and b is null.
     */
    private Boolean stringEquals(String a, String b) {
        boolean aIsNull = a == null;
        boolean bIsNull = b == null;
        if (aIsNull != bIsNull) {
            return false;
        } else {
            return aIsNull || a.equals(b);
        }
    }
}
