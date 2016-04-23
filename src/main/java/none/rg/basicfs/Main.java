package none.rg.basicfs;

/**
 * For now it is going to be used for manual-testing purposes
 * Later we shall make it entry point for utility tool
 */
public class Main {

    public static void main(String... args) {
        BasicFs fs = new BasicFs("sample.fs");
        fs.createFile("/", "file1.txt");
        fs.makeDirectory("/", "somedir");
        fs.createFile("/somedir", "executable.exe");
        fs.createFile("/", "file22.bak");
        fs.close();
    }

}
