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

    private Map<String, HashFile> files;

    /* 直接提取StagingArea中的文件 */
    public Commit(String message, String parent, Date timestamp, Map<String, HashFile> files) throws IOException {
        this.message = message;
        this.parent = parent;
        this.timestamp = timestamp;
        this.files = cache(files);
    }

    static Commit load(File file) {
        return Utils.readObject(file, Commit.class);
    }

    void dump() {
        Utils.writeObject(Utils.join(COMMIT_DIR, sha1(this)), Commit.class);
    }

    private Map<String, HashFile> cache(Map<String, HashFile> files) throws IOException {
        Map<String, HashFile> cached = new HashMap<>();
        for (String name : files.keySet()) {
            HashFile src = files.get(name);
            HashFile dest = (HashFile) join(COMMIT_DIR, src.getContentHash());
            Files.copy(src.toPath(), dest.toPath());
            cached.put(name, dest);
        }
        return cached;
    }

    // TODO: fix IOException.
}
