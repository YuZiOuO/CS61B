package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gitlet.Utils.*;

// the abstract of CWD and stagingArea Files.
class StagingArea implements Serializable {
    public static final String STAGING_AREA_FILENAME = Repository.STAGING_AREA_FILENAME;

    private String prevCommitHash;
    private Map<String,Blob> workTree;

    //for checkout
    public void checkout(String commitHash) {
        Commit c = Commit.load(commitHash);
        this.prevCommitHash = c.getParent();
        this.workTree = Commit.convertToBlobTree(c.getHash());
        List<String> allFiles = plainFilenamesIn(Repository.CWD);
        for(String file : allFiles) {
            File f = join(Repository.CWD, file);
            f.delete();
        }
        for(String file : workTree.keySet()) {
            restore(file);
        }
    }

    StagingArea(){
        prevCommitHash = null;
        workTree = new HashMap<>();
    }

    void add(String name){
        File src = join(Repository.CWD, name);
        Blob working = workTree.get(name);
        workTree.put(name,Blob.push(src));
        if (working != null) {
            working.pop();
        }
    }

    // if
    void remove(String name){
        File file = join(Repository.CWD, name);
        Blob prev = Commit.load(prevCommitHash).getBlob(name);
        Blob working = workTree.get(name);
        if (working.getHash().equals(prev.getHash())) {
            // tracked and not modified
            workTree.put(name,null);
            file.delete();
        }else{
            // modified
            workTree.put(name, prev);
            writeContents(file,prev.content());
            prev.ref();
        }
        working.pop();
    }

    //reset a file content to a blob
    //if blob == null, set to blob in current tracked version
    void reset(String name,Blob blob){
        File dest = join(Repository.CWD, name);
        workTree.get(name).pop();
        if(blob == null){
            Commit prev = Commit.load(prevCommitHash);
            blob = Blob.load(prev.getFiles().get(name));
        }
        workTree.put(name,blob);
        blob.ref();
        writeContents(dest,blob.content());
    }

    //reset a file content to the staged version
    void restore(String filename){
        File dest = join(Repository.CWD, filename);
        Blob working = workTree.get(filename);
        writeContents(dest,working.content());
    }

    boolean contains(String filename){
        return filename != null && workTree.containsKey(filename);
    }

    Commit toCommit(String message, String parent, Date timestamp) {
        for(String filename: workTree.keySet()) {
            // delete the file if staged for removal
            if(workTree.get(filename) == null) {
                workTree.remove(filename);
            }
        }
        Commit c = new Commit(message, parent, timestamp, workTree);
        for(Blob f : workTree.values()) {
            f.ref();
        }
        prevCommitHash = c.getHash();
        return c;
    }

    static StagingArea load(){
        return readObject(join(Repository.GITLET_DIR,STAGING_AREA_FILENAME)
                ,StagingArea.class);
    }

    void dump(){
        writeObject(join(Repository.GITLET_DIR,STAGING_AREA_FILENAME),this);
    }

    //everything is as previous commit
    boolean workTreeClean(){
        List<String> allFiles = plainFilenamesIn(Repository.CWD);
        Commit prev = Commit.load(prevCommitHash);
        Map<String,String> prevFiles = prev.getFiles();
        if(allFiles == null){
            // Only does the root commit has no files
            return prev.getParent() == null;
        }else{
            // Check if all files are added
            for(String filename : allFiles) {
                if(!workTree.containsKey(filename)) {
                    return false;
                }
            }
        }
        return nothingToCommit();
    }

    boolean nothingToCommit(){
        for(String filename : workTree.keySet()) {
            // Check if all staged files are tracked
            Commit prev = Commit.load(prevCommitHash);
            Map<String,String> prevFiles = prev.getFiles();
            if(workTree.get(filename) == null ||
                    !workTree.get(filename).getHash().equals(
                            prevFiles.get(filename))){
                return false;
            }
        }
        return true;
    }
    boolean stagedForRemoval(String filename){
        Commit prev = Commit.load(prevCommitHash);
        return prev.contain(filename) && workTree.get(filename) == null;
    }
}
