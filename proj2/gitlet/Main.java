package gitlet;

import java.io.File;

import static gitlet.Repository.GITLET_DIR;
import static gitlet.Utils.join;
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
        if (args.length == 0) {
            message("Please enter a command.");
            System.exit(0);
        }

        if (!Config.ARGUMENT_CONFIG.containsKey(args[0])) {
            message("No command with that name exists.");
            System.exit(0);
        }

        if (!Config.ARGUMENT_CONFIG.get(args[0]).contains(args.length)) {
            message("Incorrect operands.");
            System.exit(0);
        }

        File f = join(GITLET_DIR, Config.REPO_FILENAME);
        if (!args[0].equals("init") && !f.exists()) {
            message("Not in an initialized Gitlet directory.");
            System.exit(0);
        }

        switch (args[0]) {
            // sort by frequency of usage
            case "add":
                Handler.add(args[1]);
                break;
            case "rm":
                Handler.rm(args[1]);
                break;
            case "status":
                Handler.status();
                break;
            case "reset":
                Handler.reset(args[1]);
                break;
            case "commit":
                if (args.length == 1) {
                    message("Please enter a commit message.");
                } else {
                    Handler.commit(args[1]);
                }
                break;
            case "checkout":
                if (args.length == 2) {
                    Handler.checkoutBranch(args[1]);
                } else if (args[1].equals("--")) {
                    Handler.checkoutCherryPick(args[2], null);
                } else if (args[2].equals("--")) {
                    Handler.checkoutCherryPick(args[3], args[1]);
                } else {
                    message("Incorrect operands.");
                }
                break;
            case "branch":
                Handler.branch(args[1]);
                break;
            case "rm-branch":
                Handler.rmBranch(args[1]);
                break;
            case "merge":
                Handler.merge(args[1]);
                break;
            case "log":
                Handler.log();
                break;
            case "global-log":
                Handler.globalLog();
                break;
            case "find":
                Handler.find(args[1]);
                break;
            case "init":
                Handler.init();
        }
    }
}
