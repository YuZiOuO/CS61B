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
        // TODO: what if args is empty?
        // TODO: args checking(check numbers of args)

        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                Handler.init();
                break;
            case "add":
                Handler.add(args[1]);
                break;
            case "commit":
                Handler.commit(args[1]);
                break;
            case "rm":
                Handler.rm(args[1]);
                break;
            case "checkout":
                Handler.checkout(args[1],args[2],null); //TODO
                break;
        }
    }
}
