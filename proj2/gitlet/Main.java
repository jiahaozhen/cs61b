package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Jia Haozhen
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                validateArgs(args, 1);
                Repository.init();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                validateArgs(args, 2);
                Repository.add(args[1]);
                break;
            // TODO: FILL THE REST IN
            case "commit":
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
        }
    }

    public static void validateArgs(String[] args, int n) {
        if (args.length != n) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }
}
