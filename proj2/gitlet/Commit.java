package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.*;

/**
 * Represents a gitlet commit object.
 *
 * @author CYZ
 */
public class Commit implements Serializable {
    static File COMMIT_DIR = Utils.join(Repository.GITLET_DIR, "commit");

    public String getMessage() {
        return message;
    }

    /**
     * The message of this Commit.
     */
    private final String message;

    public String getParent() {
        return parent;
    }

    private final String parent;

    public Date getTimestamp() {
        return timestamp;
    }

    private final Date timestamp;

    public Map<String, String> getFiles() {
        return files;
    }

    private final Map<String, String> files;

    public String getHash() {
        return hash;
    }

    private final String hash;

    /* 直接提取StagingArea中的文件 */
    Commit(String message, String parent, Date timestamp, Map<String, Blob> files){
        this.message = message;
        this.parent = parent;
        this.timestamp = timestamp;
        this.files = cache(files);
        this.hash = sha1(this.toString());
    }

    /**
     * Load a commit with given hash.
     * The corresponding {@code Commit} mush been dumped in advance,
     *
     * @param hash the commit hash.
     * @throws IllegalArgumentException if the corresponding commit files
     * does not exist or can't be read.
     * @return null if {@code hash == null},otherwise the corresponding {@code Commit}.
     */
    static Commit load(String hash) {
        return hash == null?null:Utils.readObject(join(COMMIT_DIR,hash), Commit.class);
    }

    void dump() {
        Utils.writeObject(Utils.join(COMMIT_DIR, hash), this);
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

    String toLog(){
        StringBuilder sb = new StringBuilder();
        Formatter f = new Formatter(sb, Locale.US);
        f.format("===\ncommit ").format(this.getHash()).format("\nDate: ")
                .format("%1$ta %1$tb %1$td %1$tT %1$tY %1$tz\n",this.getTimestamp())
                .format(this.getMessage()).format("\n\n");
        return sb.toString();
    }

    //for checkout
    static Map<String,Blob> convertToBlobTree(String hash){
        Map<String,String> src = Commit.load(hash).files;
        Map<String,Blob> dest = new HashMap<>();
        for(String filename: src.keySet()) {
            Blob b = Blob.load(src.get(filename));
            dest.put(filename,b);
        }
        return dest;
    }

    Commit findParent(Commit from,String target){
        // Note: load() invokes Utils.readObjects,which throws an error if the file does not exist.
        if(this.equals(from)) return this;
        if(this.parent == null) return null;
        if(matchHash(target, this.parent)){
            return Commit.load(this.parent);
        }
        return findParent(Commit.load(this.parent),target);
    }



}
