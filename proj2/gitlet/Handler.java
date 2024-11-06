package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static gitlet.Utils.*;

public class Handler {
    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    static boolean repoExists(){
        return GITLET_DIR.exists() && GITLET_DIR.isDirectory();
    }

    static void init() throws IOException {
        if(repoExists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            return;
        }
        GITLET_DIR.mkdir();
        Repository repo = new Repository();
        repo.dump();
    }

    static void add(Repository repo,String[] name) throws IOException {
        List<String> allFiles = plainFilenamesIn(CWD);
        for(String n:name){
            if(!allFiles.contains(n)){
                System.out.println("File does not exist.");
                continue;
            }
            repo.stagingArea.add(name);
        }
    }

    static void commit(Repository repo,String message) throws IOException {
        repo.commit(message, now());
    }

    static void rm(File file){

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
