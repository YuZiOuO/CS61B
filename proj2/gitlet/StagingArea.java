package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gitlet.Utils.*;

class StagingArea implements Serializable {
    public static final String STAGING_AREA_FILENAME = Repository.STAGING_AREA_FILENAME;

    private Map<String,Blob> prevTree;
    private Map<String,Blob> workTree;

    //for checkout
    public void setTree(Map<String, Blob> Tree) {
        this.prevTree = Tree;
        this.workTree = Tree;
    }

    StagingArea(){
        prevTree = new HashMap<>();
        workTree = new HashMap<>();
    }

    void add(String name){
        if (name == null) {
            return;
        }
        File src = join(Repository.CWD, name);
        Blob prev = workTree.get(name);
        workTree.put(name,Blob.push(src));
        if (prev != null) {
            prev.pop();
        }
    }

    void addAll(){
        List<String> allFiles = plainFilenamesIn(Repository.CWD);
        if (allFiles != null) {
            for (String filename : allFiles) {
                this.add(filename);
            }
        }
    }

    void remove(String name){
        if(name == null) {
            return;
        }
        File src = join(Repository.CWD, name);
        Blob working = workTree.get(name);
        if (working.getHash().equals(sha1(readContents(src)))) {
            workTree.put(name,null);
            working.pop();
            src.delete();
        }else{
            Blob prev = prevTree.get(name);
            workTree.put(name, prev);
            working.pop();
            prev.ref();
        }
    }

    //reset a file content to a blob
    //if blob == null , set to blob in current version
    void reset(String name,Blob blob){
        if(name == null) {
            return;
        }
        File src = join(Repository.CWD, name);
        Blob working = workTree.get(name);
        if(blob == null){
            blob = prevTree.get(name);
        }
        workTree.put(name,blob);
        working.pop();
        blob.ref();
        writeContents(src,blob.content());
    }

    boolean contains(String name){
        return name != null && workTree.containsKey(name);
    }

    Commit toCommit(String message, String parent, Date timestamp) {
        prevTree = new HashMap<>(workTree);
        for(Blob f : workTree.values()) {
            f.ref();
        }
        return new Commit(message, parent, timestamp, workTree);
    }

    static StagingArea load(){
        return readObject(join(Repository.GITLET_DIR,STAGING_AREA_FILENAME)
                ,StagingArea.class);
    }

    void dump(){
        writeObject(join(Repository.GITLET_DIR,STAGING_AREA_FILENAME),this);
    }

    //O(n)
    boolean allFilesTracked(){
        List<String> allFiles = plainFilenamesIn(Repository.GITLET_DIR);
        if(allFiles == null){
            return prevTree.isEmpty();
        }
        for(String filename : allFiles) {
            Blob blob = prevTree.get(filename);
            File src = join(Repository.CWD, filename);
            if(blob == null || !blob.getHash().equals(sha1(readContents(src)))){
                return false;
            }
        }
        return true;
    }
}
