package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.*;

import static gitlet.Utils.*;

/**
 * Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 * @author TODO
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

    private Map<String, Blob> files;

    /* 直接提取StagingArea中的文件 */
    Commit(String message, String parent, Date timestamp, Map<String, Blob> files) throws IOException {
        this.message = message;
        this.parent = parent;
        this.timestamp = timestamp;
        this.files = cache(files);
    }

    static Commit load(File file) {
        return Utils.readObject(file, Commit.class);
    }

    void dump() {
        Utils.writeObject(Utils.join(COMMIT_DIR, sha1(this.toString())), Commit.class);
    }

    private Map<String, Blob> cache(Map<String, Blob> files) throws IOException {
        for (Blob b: files.values()) {
            b.ref();
        }
        return files;
    }

    @Override
    public String toString() {
        return "Commit@"+timestamp.toString()+"\nmessage:"+message+"\nparent:"+parent
                +"\nfiles:"+files.toString();
    }
    // TODO: fix IOException.
}
