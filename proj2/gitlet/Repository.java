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

    public static final String INITIAL_COMMIT_MESSAGE = "initial commit";

    private String HEAD;
    private String currentBranch;

    private Map<String, String> refs;

    private TreeSet<String> commitTree;

    private File pathOfCommit;

    StagingArea stagingArea;

    public Repository() throws IOException {
        refs = new TreeMap<>();
        commitTree = new TreeSet<>();
        stagingArea = new StagingArea();
        stagingArea.addAll();
        commit(INITIAL_COMMIT_MESSAGE,Date.from(Instant.EPOCH));
    }

    public void commit(String message, Date timestamp) throws IOException {
        Commit c = stagingArea.extract(message,refs.get(currentBranch),timestamp);
        String hash = sha1(c);
        refs.put(currentBranch, hash);
        commitTree.add(hash);
        c.dump();
    }

    static Repository load(File file) {
        return readObject(file, Repository.class);
    };

    void dump(){
        writeObject(join(GITLET_DIR,REPO_FILENAME),Repository.class);
    };
}