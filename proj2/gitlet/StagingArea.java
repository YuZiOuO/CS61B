package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gitlet.Utils.*;

class StagingArea implements Serializable {
    private HashMap<String,Blob> previousTree;
    private final HashMap<String,Blob> workingTree;

    StagingArea(){
        previousTree = new HashMap<>();
        workingTree = new HashMap<>();
    }

    void add(String name){
        if (name == null) {
            return;
        }
        File src = join(Repository.CWD, name);
        Blob prev = workingTree.get(name);
        workingTree.put(name,Blob.push(src));
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
        Blob prev = workingTree.get(name);
        if (prev.getHash().equals(sha1(readContents(src)))) {
            workingTree.put(name,null);
            prev.pop();
            src.delete();
        }else{
            Blob last = previousTree.get(name);
            workingTree.put(name,last);
            prev.pop();
            last.ref();
        }
    }

    boolean contains(String name){
        return name != null && workingTree.containsKey(name);
    }

    Commit toCommit(String message, String parent, Date timestamp) throws IOException {
        previousTree = new HashMap<>(workingTree);
        for(Blob f : workingTree.values()) {
            f.ref();
        }
        return new Commit(message, parent, timestamp, workingTree);
    }
}
