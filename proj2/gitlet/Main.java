package gitlet;

import static gitlet.Utils.message;

/**
 * Driver class for Gitlet, a subset of the Git version-control system.
 *
 * @author CYZ
 */
public class Main {

    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND1> <OPERAND2> ...
     */
    public static void main(String[] args) {
        switch (args.length) {
            case 1:
                switch (args[0]) {
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
                        message("Please enter a commit message.");
                        break;
                    case "status":
                        Handler.status();
                        break;
                }
                break;
            case 2:
                switch (args[0]) {
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
                        Handler.checkoutBranch(args[1]);
                        break;
                    case "reset":
                        Handler.reset(args[1]);
                        break;
                }
                break;
            case 3:
                switch (args[0]) {
                    case "checkout":
                        if (args[1].equals("--")) {
                            Handler.checkoutCherryPick(args[2], null);
                        }
                        break;
                }
                break;
            case 4:
                switch (args[0]) {
                    case "checkout":
                        if (args[2].equals("--")) {
                            Handler.checkoutCherryPick(args[3], args[1]);
                        }
                }
                break;
            default:
                break;
        }
    }
}
