package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

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

    private final Map<String, String> refs;

    private final TreeSet<String> commitTree;

    private File pathOfCommit;

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
        String hash = sha1(c.toString());
        refs.put(currentBranch, hash);
        commitTree.add(hash);
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

    Commit HEAD(){
        return Commit.load(refs.get(currentBranch));
    }

    void checkoutResetFile(String name){
        stagingArea.reset(name,null);
    }
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
    void checkoutBranch(String branch){
        if(refs.containsKey(branch)){
            currentBranch = branch;
        }
        // TODO: implementation
    }
}