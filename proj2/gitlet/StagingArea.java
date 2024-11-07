package gitlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gitlet.Utils.*;

class StagingArea {
    static final File STAGING_AREA_DIR = join(Repository.GITLET_DIR, "staged");

    private static HashMap<String, Blob> workingTree;

    StagingArea(){
        workingTree = new HashMap<>();
    }

    void add(String[] name) throws IOException {
        if (name == null) {
            return;
        }
        for (String n : name) {
            File src = join(Repository.CWD, n);
            Blob prev = workingTree.get(n);
            prev.pop();
            workingTree.put(n,Blob.push(src));
        }
    }

    void addAll() throws IOException {
        List<String> allFiles = plainFilenamesIn(Repository.CWD);
        if (allFiles != null) {
            this.add(allFiles.toArray(new String[0]));
        }
    }

    void remove(String[] name){
        if(name == null) {
            return;
        }
        for(String n : name){
            File src = join(Repository.CWD, n);
            Blob prev = workingTree.get(n);
            //TODO: implementation
        }
    }

    void clear() {
        for (Blob f : workingTree.values()) {
            f.delete();
        }
        workingTree.clear();
    }

    boolean isClear() {
        return workingTree.isEmpty();
    }

    boolean contains(String name) {
        return workingTree.containsKey(name);
    }

    Commit toCommit(String message, String parent, Date timestamp) throws IOException {
        return new Commit(message, parent, timestamp, workingTree);
    }

}
