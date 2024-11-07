package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.*;

/**
 * Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 * @author CYZ
 */
public class Commit implements Serializable {
    static File COMMIT_DIR = Utils.join(Repository.GITLET_DIR, "commit");

    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /**
     * The message of this Commit.
     */
    private final String message;

    private final String parent;

    private final Date timestamp;

    private final Map<String, String> files;

    /* 直接提取StagingArea中的文件 */
    Commit(String message, String parent, Date timestamp, Map<String, Blob> files){
        this.message = message;
        this.parent = parent;
        this.timestamp = timestamp;
        this.files = cache(files);
    }

    static Commit load(String hash) {
        return Utils.readObject(join(COMMIT_DIR,hash), Commit.class);
    }

    void dump() {
        Utils.writeObject(Utils.join(COMMIT_DIR, sha1(this.toString())), this);
    }

    private Map<String, String> cache(Map<String, Blob> files){
        HashMap<String,String> _files = new HashMap<>();
        for (String name : files.keySet()) {
            Blob b = files.get(name);
            b.ref();
            _files.put(name,b.getHash());
            b.dump();
        }
        return _files;
    }

    boolean contain(String name){
        return name != null && files.containsKey(name);
    }

    Blob getBlob(String name){
        return Blob.load(files.get(name));
    }

    @Override
    public String toString() {
        return "Commit@"+timestamp.toString()+"\nmessage:"+message+"\nparent:"+parent
                +"\nfiles:"+files.toString();
    }
}
