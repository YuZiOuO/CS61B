package gitlet;

import java.io.File;
import static gitlet.Utils.*;

/* 用于内容不会变动的文件 */
class Blob extends File {
    public static final File BLOB_DIR = join(Repository.GITLET_DIR, "blob");

    private final String hash;
    private final byte[] buf;
    private int refs;

    private Blob(File f){
        super(join(BLOB_DIR,sha1(readContents(f))).getPath());
        buf = readContents(f);
        hash = sha1(buf);
        refs = 1;
    }

    //stagingArea
    static Blob push(File f){
        String hash = sha1(readContents(f));
        File blob = new File(BLOB_DIR,hash);
        if(!blob.exists()){
            Blob b = new Blob(f);
            b.dump();
            return b;
        }else{
            Blob b = readObject(blob,Blob.class);
            b.refs++;
            return b;
        }
    }
    int pop(){
        refs--;
        if(refs == 0){
            this.delete();
        }
        return refs;
    }

    String getHash(){
        return hash;
    }

    void ref(){
        refs++;
    }

    byte[] content(){
        return buf;
    }

    //filesystem
    static Blob load(String hash){
        return readObject(join(BLOB_DIR,hash),Blob.class);
    }

    void dump(){
        writeObject(join(BLOB_DIR,hash),this);
    }
}
