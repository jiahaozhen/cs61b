package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Jia Haozhen
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                validateArgs(args, 1);
                Repository.init();
                break;
            case "add":
                validateArgs(args, 2);
                Repository.add(args[1]);
                break;
            case "commit":
                validateArgs(args, 2);
                Repository.commit(args[1]);
                break;
            case "rm":
                validateArgs(args, 2);
                Repository.rm(args[1]);
                break;
            case "log":
                validateArgs(args, 1);
                Repository.log();
                break;
            case "global-log":
                validateArgs(args, 1);
                Repository.globalLog();
                break;
            case "find":
                validateArgs(args, 2);
                Repository.find(args[1]);
                break;
            case "status":
                validateArgs(args, 1);
                Repository.status();
                break;
            case "checkout":
                if (args.length == 2) {
                    Repository.checkoutBranch(args[1]);
                } else if (args.length == 3) {
                    checkargs(args[1], "--");
                    Repository.checkoutFile(args[2]);
                } else if (args.length == 4) {
                    checkargs(args[2], "--");
                    Repository.checkoutFile(args[1], args[3]);
                } else {
                    validateArgs(args, 2);
                }
                break;
            case "branch":
                validateArgs(args, 2);
                Repository.branch(args[1]);
                break;
            case "rm-branch":
                validateArgs(args, 2);
                Repository.removeBranch(args[1]);
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

    private static void checkargs(String source, String target) {
        if (!source.equals(target)) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }
}
