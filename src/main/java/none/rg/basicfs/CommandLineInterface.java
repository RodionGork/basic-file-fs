package none.rg.basicfs;

import none.rg.basicfs.blocks.HeaderBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CommandLineInterface {

    private BasicFs fs;

    public static void main(String... args) {
        new CommandLineInterface().run(args);
    }

    private void run(String... args) {
        if (args.length < 1) {
            System.out.println("File-system file-name should be specified as the first command-line argument");
            return;
        }
        fs = new BasicFs(args[0]);
        if (args.length == 1) {
            interactive();
        }
        fs.close();
    }

    private void interactive() {
        Scanner input = new Scanner(System.in);
        String currentPath = "";
        while (true) {
            System.out.print("> ");
            System.out.flush();
            List<String> line = split(input.nextLine());
            if (line.size() < 1) {
                continue;
            }
            String command = line.get(0).toLowerCase();
            if (command.matches("exit|quit|bye")) {
                break;
            }
            if (command.matches("ls|dir")) {
                System.out.println(fs.list(currentPath));
            }
            if (command.matches("cd|chdir")) {
                currentPath = changeDirectory(currentPath, line.size() > 1 ? line.get(1) : "");
                System.out.println("Current directory is: " + (currentPath.isEmpty() ? "/" : "") + currentPath);
            }
            if (command.matches("\\?|help")) {
                System.out.println("Try commands: dir, cd, md, put, get, delete, deltree");
            }
            ftpLikeCommands(currentPath, line, command);
            deletionCommands(currentPath, line, command);
        }
    }

    private void deletionCommands(String currentPath, List<String> line, String command) {
        if (command.equals("delete")) {
            if (line.size() > 1) {
                fs.delete(currentPath + "/" + line.get(1));
                System.out.println("Entry was deleted");
            }
        }
        if (command.equals("deltree")) {
            if (line.size() > 1) {
                int total = fs.getUtils().deleteTree(currentPath + "/" + line.get(1));
                System.out.println("Total entities deleted: " + total);
            }
        }
    }

    private void ftpLikeCommands(String currentPath, List<String> line, String command) {
        if (command.matches("md|mkdir")) {
            if (line.size() > 1) {
                fs.makeDirectory(currentPath, line.get(1));
                System.out.println("Directory was created");
            }
        }
        if (command.equals("put")) {
            if (line.size() > 1) {
                int total = fs.getUtils().put(currentPath, line.get(1));
                System.out.println("Total entities written: " + total);
            }
        }
        if (command.equals("get")) {
            if (line.size() > 1) {
                int total = fs.getUtils().get(currentPath + "/" + line.get(1), System.getProperty("user.dir"));
                System.out.println("Total entities fetched: " + total);
            }
        }
    }

    private String changeDirectory(String currentPath, String to) {
        if (to.equals("..")) {
            return currentPath.replaceFirst("(.*)\\/[^\\/]+", "$1");
        }
        String newPath = currentPath + "/" + to;
        if (!HeaderBlock.Type.DIRECTORY.equals(fs.fileType(newPath))) {
            System.out.println("Warning, no such name found: " + to);
            return currentPath;
        }
        return newPath.replaceFirst("\\/$", "");
    }

    private List<String> split(String s) {
        List<String> result = new ArrayList<>();
        Scanner in = new Scanner(s);
        while (in.hasNext()) {
            result.add(in.next());
        }
        return result;
    }

}
