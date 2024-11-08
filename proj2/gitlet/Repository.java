package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.time.Instant;
import java.util.*;

import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author CYZ
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

    private final Set<String> commitTree;

    private String entry;

    //cache before dump
    StagingArea stagingArea;

    public Repository() throws IOException {
        refs = new TreeMap<>();
        currentBranch = "master";
        commitTree = new TreeSet<>();
        stagingArea = new StagingArea();
        stagingArea.addAll();
        if(Commit.COMMIT_DIR.mkdirs() && Blob.BLOB_DIR.mkdirs()) {
            commit(INITIAL_COMMIT_MESSAGE, Date.from(Instant.EPOCH));
        }
    }

    public void commit(String message, Date timestamp) throws IOException {
        Commit c = stagingArea.toCommit(message,currentBranch,timestamp);
        entry = sha1(c.toString());
        refs.put(currentBranch, entry);
        commitTree.add(entry);
        c.dump();
    }

    static Repository load(File file) {
        Repository repo = readObject(file, Repository.class);
        repo.stagingArea = StagingArea.load();
        return repo;
    }

    void dump(){
        stagingArea.dump();
        stagingArea = null;
        writeObject(join(GITLET_DIR,REPO_FILENAME),this);
    }

    /**
     * return the latest commit of the currentBranch.
     *
     * @return a Commit.
     */
    Commit HEAD(){
        return Commit.load(refs.get(currentBranch));
    }

    /**
     * Reset the file with given name to the last commited version.
     *
     * @param name The filename to reset.
     * @return 0 for success, 1 if the given file is untracked.
     */
    int checkoutResetFile(String name){
        Commit c = Commit.load(refs.get(currentBranch));
        if(!c.contain(name)){
            return 1;
        }
        stagingArea.reset(name,null);
        return 0;
    }

    /**
     * Cherry-pick the given file in given commit to CWD, overwrite the currently
     * existing one.
     *
     * @param commit The commit which the file to be extracted from.
     * @param name The file name.
     * @return 0 for success, 1 if file does not exist, and 2 if commit does not exist.
     */
    int checkoutCherryPickFile(String commit,String name){
        if(commitTree.contains(commit)){
            Commit c = Commit.load(commit);
            if(c.contain(name)){
                Blob b = c.getBlob(name);
                stagingArea.reset(name,b);
                return 0;
            }
            return 1;
        }
        return 2;
    }

    /**
     * Checkout to the given branch, clear all files in CWD and replace with corresponding files
     * contained in the last commit of the given branch.
     *
     * @param branch The branch to checkout.
     * @return 0 for success, 1 if branch does not exist, 2 for no need to checkout,3 for untracked files;
     */
    int checkoutBranch(String branch){
        if(currentBranch.equals(branch)){
            return 2;
        }
        if(refs.containsKey(branch)){
            if(!stagingArea.allFilesTracked()){
                return 3;
            }
            currentBranch = branch;
            stagingArea.setTree(Commit.convertToBlobTree(refs.get(branch)));
            List<String> allFiles = plainFilenamesIn(CWD);
            if (allFiles != null) {
                for(String f : allFiles){
                    stagingArea.reset(f,null);
                }
            }
            return 0;
        }
        return 1;
    }
}