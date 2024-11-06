package gitlet;

import java.io.IOException;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) throws IOException {
        // TODO: what if args is empty?

        // TODO: args checking(check numbers of args)
        if(!argCheck(args)) {
            System.exit(0);
        }

        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                try{
                    Handler.init();
                }catch(Exception e) {
                    e.printStackTrace();
                }
                break;
            case "add":
                Repository repo = Handler.loadRepository();
                Handler.add(repo,new String[] {args[1]});
                break;
            // TODO: FILL THE REST IN
        }
    }

    // TODO: args checking
    public static boolean argCheck(String[] args) {
        return true;
    }
}
