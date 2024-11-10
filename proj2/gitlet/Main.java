package gitlet;

import java.io.IOException;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author CYZ
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) throws IOException {
        switch(args.length){
            case 1:
                switch(args[0]){
                    case "init":
                        Handler.init();
                        break;
                    case "log":
                        Handler.log();
                        break;
                    case "global-log":
                        Handler.globalLog();
                        break;
                    case "commit":
                        System.out.println("Please enter a commit message.");
                        break;
                }
                break;
            case 2:
                switch(args[0]){
                    case "add":
                        Handler.add(args[1]);
                        break;
                    case "commit":
                        Handler.commit(args[1]);
                        break;
                    case "rm":
                        Handler.rm(args[1]);
                        break;
                    case "find":
                        Handler.find(args[1]);
                        break;
                    case "branch":
                        Handler.branch(args[1]);
                        break;
                    case "rm-branch":
                        Handler.rmBranch(args[1]);
                        break;
                    case "checkout":
                        Handler.checkoutbranch(args[1]);
                        break;
                }
                break;
            case 3:
                switch(args[0]){
                    case "checkout":
                        if(args[1].equals("--")){
                            Handler.checkoutRestore(args[2]);
                        }
                        break;
                }
                break;
            case 4:
                switch(args[0]){
                    case "checkout":
                        if(args[2].equals("--")){
                            Handler.checkoutCherryPick(args[1],args[3]);
                        }
                }
                break;
            default:
                break;
        }
    }
}
