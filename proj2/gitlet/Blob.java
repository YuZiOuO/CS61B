package gitlet;

import java.io.File;

import static gitlet.Utils.*;

// abstract of staged and tracked files
class Blob extends File {
    public static final File BLOB_DIR = join(Repository.GITLET_DIR, "blob");

    final String hash;
    final byte[] content;
    private int refs;

    private Blob(String hash, byte[] content){
        super(join(BLOB_DIR, hash).getPath());
        this.content = content;
        this.hash = hash;
        refs = 1;
    }

    static Blob push(byte[] content){
        String hash = sha1(content);
        File blob = new File(BLOB_DIR, hash);
        if (!blob.exists()) {
            Blob b = new Blob(hash,content);
            b.dump();
            return b;
        } else {
            Blob b = readObject(blob, Blob.class);
            b.refs++;
            return b;
        }
    }

    static Blob push(File f) {
        byte[] content = readContents(f);
        return push(content);
    }

    int pop() {
        refs--;
        if (refs == 0) {
            this.delete();
        }
        return refs;
    }

    void ref() {
        refs++;
    }

    static Blob load(String hash) {
        return readObject(join(BLOB_DIR, hash), Blob.class);
    }

    void dump() {
        writeObject(join(BLOB_DIR, hash), this);
    }
}
