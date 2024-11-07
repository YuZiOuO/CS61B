package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static gitlet.Utils.*;

public class Handler {
    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    static Repository loadRepository(){
        return Repository.load(join(GITLET_DIR,Repository.REPO_FILENAME));
    }

    static void init() throws IOException {
        if(GITLET_DIR.mkdirs()){
            Repository repo = new Repository();
            repo.dump();
        }else{
            System.out.println("A Gitlet version-control system already exists in the current directory.");
        }
    }

    static void add(String name) throws IOException {
        List<String> allFiles = plainFilenamesIn(CWD);
        if (allFiles != null && !allFiles.contains(name)) {
            System.out.println("File does not exist.");
        }
        loadRepository().stagingArea.add(name);
    }

    static void commit(String message) throws IOException {
        loadRepository().commit(message, now());
    }

    static void rm(String name){
        Repository _repo = loadRepository();
        StagingArea stagingArea = _repo.stagingArea;
        if(!stagingArea.contains(name) && !_repo.HEAD().contain(name)){
            System.out.println("No reason to remove the file.");
            return;
        }
        _repo.stagingArea.remove(name);
    }

    static void log(){

    }

    static void globalLog(){

    }

    static void find(String message){

    }

    static void status(){}

    //need overload
    static void checkout(){}

    static void branch(String name){}

    static void rmBranch(String name){};

    static void reset(String hash){};

    static void merge(String name){}
}
