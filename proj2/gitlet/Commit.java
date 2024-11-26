package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.join;
import static gitlet.Utils.sha1;

/**
 * Represents a gitlet commit.
 */
public class Commit implements Serializable {

    static File COMMIT_DIR = Utils.join(Repository.GITLET_DIR, "commit");

    /**
     * The message of this Commit.
     */
    final String message;
    /**
     * The hash code of this commit.
     */
    final String hash;
    /**
     * The parents of this commit.
     */
    final List<String> parent;
    /**
     * The Date object representing commit time.
     */
    private final Date timestamp;
    /**
     * The map representing all files tracked by this commit.
     */
    private final Map<String, String> files;

    Date getTimestamp() {
        return (Date) timestamp.clone();
    }

    /**
     * Create a Commit object using the given params.
     *
     * @param message   the commit message.
     * @param parent    the commit parent,passed as an array to support multi.
     * @param timestamp the Date object representing commit time.
     * @param files     the Map<String,Blob> representing all files tracked by the commit.
     */
    Commit(String message, String[] parent, Date timestamp, Map<String, Blob> files) {
        this.message = message;
        this.parent = new ArrayList<>();
        this.parent.addAll(Arrays.asList(parent));
        this.timestamp = timestamp;
        this.files = cache(files);
        this.hash = sha1(this.toString());
    }

    /**
     * From the given commit,
     * Load a work tree with the format < String,String > to a Blob tree of < String, Blob >.
     *
     * @param commitHash the given commit hash.
     * @return a blob tree.
     */
    static Map<String, Blob> loadWorkTree(String commitHash) {
        Map<String, String> src = Commit.load(commitHash).files;
        return loadWorkTree(src);
    }

    /**
     * Load a work tree with the format < String,String > to a Blob tree of < String, Blob >.
     *
     * @param hashTree the given tree.
     * @return a blob tree.
     */
    static Map<String, Blob> loadWorkTree(Map<String, String> hashTree) {
        Map<String, Blob> dest = new HashMap<>();
        for (String filename : hashTree.keySet()) {
            Blob b = Blob.load(hashTree.get(filename));
            dest.put(filename, b);
        }
        return dest;
    }

    /**
     * Load a commit with given hash.
     * The corresponding {@code Commit} mush been dumped in advance,Otherwise throw an error.
     *
     * @param hash the commit hash.
     * @return null if {@code hash == null},otherwise the corresponding {@code Commit}.
     */
    static Commit load(String hash) {
        return hash == null ? null : Utils.readObject(join(COMMIT_DIR, hash), Commit.class);
    }

    String getMessage() {
        return message;
    }

    Map<String, String> getFiles() {
        return new HashMap<>(files);
    }

    /**
     * Interacting with the Blob class,cached all files in the given tree to blob.
     *
     * @param files the file tree.
     * @return a map from file name to the cached blob hash.
     */
    private Map<String, String> cache(Map<String, Blob> files) {
        HashMap<String, String> _files = new HashMap<>();
        for (String name : files.keySet()) {
            Blob b = files.get(name);
            b.ref();
            _files.put(name, b.hash);
            b.dump();
        }
        return _files;
    }

    /**
     * Traverse the commit tree using BFS,get a map from commits to their child
     * where commits are all entry's ancestors.
     * Asymptotic: O(n) where n is the number of {@code from}'s ancestor.
     *
     * @param entry The entry of the traverse.
     * @return A map which maps commits to their child.Entry is mapped to itself.
     */
    static Map<String, String> traverse(String entry) {
        Queue<String> traversalQueue = new ArrayDeque<>();
        HashMap<String, String> childOf = new HashMap<>();
        HashSet<String> marked = new HashSet<>();

        traversalQueue.add(entry);
        childOf.put(entry, entry);

        while (!traversalQueue.isEmpty()) {
            String current = traversalQueue.poll();
            Commit parent = load(current);
            for (String p : parent.parent) {
                if (p != null && !marked.contains(p)) {
                    traversalQueue.add(p);
                    marked.add(p);
                    childOf.put(p, current);
                }
            }
        }
        return childOf;
    }

    /**
     * Find the split point(defined in proj2 webpage) of two commits.
     *
     * @return A string representing the split point hash.
     */
    static String splitPoint(String commit1, String commit2) {
        Map<String, String> traversed = traverse(commit1);
        Commit iter = Commit.load(commit2);
        while (traversed.get(iter.hash) == null) {
            iter = Commit.load(iter.parent.get(0));
        }
        return iter.hash;
    }

    /**
     * Test if the commit contain the given file.
     *
     * @param name the file to test.
     * @return whether the file is included.
     */
    boolean contain(String name) {
        return files.containsKey(name);
    }

    /**
     * Convert the Commit object to a human-readable format.
     *
     * @return the content of the commit.
     */
    @Override
    public String toString() {
        return "Commit@" + timestamp.toString() + "\nmessage:" + message + "\nparent:" + parent
                + "\nfiles:" + files.toString();
    }

    /**
     * Covert the commit to log format. An example:<br>
     * ===<br>
     * commit e881c9575d180a215d1a636545b8fd9abfb1d2bb<br>
     * Date: Wed Dec 31 16:00:00 1969 -0800<br>
     * initial commit<br>
     *
     * @return a log string.
     */
    String toLog() {
        StringBuilder sb = new StringBuilder();
        Formatter f = new Formatter(sb, Locale.US);
        f.format("===\ncommit ").format(this.hash).format("\nDate: ")
                .format("%1$ta %1$tb %1$td %1$tT %1$tY %1$tz\n", this.getTimestamp())
                .format(this.getMessage()).format("\n\n");
        return sb.toString();
    }

    /**
     * Dump the commit to file and save.
     */
    void dump() {
        Utils.writeObject(Utils.join(COMMIT_DIR, hash), this);
    }
}
