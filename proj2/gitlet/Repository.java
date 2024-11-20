package gitlet;

import java.io.File;
import java.io.Serializable;
import java.time.Instant;
import java.util.*;

import static gitlet.Utils.*;

/**
 * Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 * @author CYZ
 */
public class Repository implements Serializable {

    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    public static final String REPO_FILENAME = "repo";
    public static final String STAGING_AREA_FILENAME = "staged";
    public static final String INITIAL_COMMIT_MESSAGE = "initial commit";

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
            commit(INITIAL_COMMIT_MESSAGE, Date.from(Instant.EPOCH));
        }
    }

    public void commit(String message, Date timestamp) {
        multiParentCommit(message,timestamp,new String[]{refs.get(currentBranch)});
    }

    private void multiParentCommit(String message,Date timestamp,String[] parent) {
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
        writeObject(join(GITLET_DIR, REPO_FILENAME), this);
    }

    /**
     * Cherry-pick the given file in given commit to CWD, overwrite the currently
     * existing one.
     *
     * @param commit The commit which the file to be extracted from.
     * @param name   The file name.
     */
    void checkoutCherryPickFile(String name, String commit) {
        stagingArea.restore(name, commit == null ? null : Commit.load(commit).getBlob(name));
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
        List<String> commits = plainFilenamesIn(Commit.COMMIT_DIR);
        StringBuilder sb = new StringBuilder();
        if (commits != null) {
            for (String n : commits) {
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
        return sb.toString();
    }

    void reset(String commitHash) {
        refs.put(currentBranch, commitHash);
        stagingArea.checkout(commitHash);
    }

    //O(n) TODO:optimization
    /**
     * find if any hash of commits of this repository matches the given substring hash.
     * A naive implementation with asymptotic O(n).
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

    String getReference(String ref) {
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

        sb.append("\n=== Staged Files ===")
                .append(stagingArea.filesStaged().toString()
                        .replace("[", "\n")
                        .replaceAll(",", "\n")
                        .replace("]", "\n"));

        sb.append("\n=== Removed Files ===")
                .append(stagingArea.filesStagedForRemoval().toString()
                        .replace("[", "\n")
                        .replaceAll(",", "\n")
                        .replace("]", "\n"));

        sb.append("\n=== Modifications Not Staged For Commit ===")
                .append(stagingArea.filesUnstagedForRemoval().toString()
                        .replace("[", "\n")
                        .replaceAll(",", " (deleted)\n")
                        .replace("]", " (deleted)\n"))
                .append(stagingArea.filesUnstagedForModification().toString()
                        .replace("[", "")
                        .replaceAll(",", " (modified)\n")
                        .replace("]", " (modified)\n"));

        sb.append("\n=== Untracked Files ===")
                .append(stagingArea.filesUntracked().toString()
                        .replace("[", "\n")
                        .replaceAll(",", "\n")
                        .replace("]", "\n"));

        return sb.toString();
    }

    void merge(String branch){
        String splitPoint = Commit.splitPoint(refs.get(currentBranch),branch);
        if(splitPoint.equals(refs.get(branch))){
            message("Given branch is an ancestor of the current branch.");
            refs.put(branch, refs.get(currentBranch));
        }else if(splitPoint.equals(refs.get(currentBranch))){
            message("Current branch fast-forwarded.");
            stagingArea.checkout(refs.get(branch));
            refs.put(currentBranch, refs.get(branch));
        }else{
            Map<String,Blob> splitTree = Commit.loadWorkTree(splitPoint);
            Map<String,String> currentTree = Commit.load(getReference(currentBranch)).getFiles();
            Map<String,String> givenTree = Commit.load(getReference(branch)).getFiles();

            for(String f:givenTree.keySet()){
                String givenHash = givenTree.get(f);
                String currentHash = currentTree.get(f);
                String splitHash = splitTree.get(f).hash;
                if(!currentTree.containsKey(f)){
                    // TODO:improve if logic.
                    //在当前分支被删除
                    continue;
                }
                if(splitHash.equals(givenHash)){
                    //给定分支未修改，当前分支已/未修改
                    continue;
                }
                if(!splitHash.equals(currentHash)){
                    //文件冲突
                    Blob given = readObject(join(Blob.BLOB_DIR,givenHash),Blob.class);
                    Blob current = readObject(join(Blob.BLOB_DIR,currentHash), Blob.class);
                    StringBuilder sb = new StringBuilder();
                    sb.append("<<<<<<< HEAD\n")
                            .append(Arrays.toString(current.content))
                            .append("=======\n")
                            .append(Arrays.toString(given.content))
                            .append(">>>>>>>\n");
                    Blob conflict = Blob.push(sb.toString().getBytes());
                    currentTree.put(f, conflict.hash);
                    continue;
                }
                //给定分支已修改，当前分支未修改
                currentTree.put(f,givenTree.get(f));
            }
            for(String f:currentTree.keySet()){
                if(!givenTree.containsKey(f)){
                    //在给定分支被删除
                    currentTree.remove(f);
                }
            }

            stagingArea.checkout(refs.get(currentBranch),Commit.loadWorkTree(currentTree));
            String[] parents = new String[]{currentBranch,branch};
            multiParentCommit("Merged "+branch+" into "+currentBranch
                    ,Date.from(Instant.now()),parents);
        }
    }
}