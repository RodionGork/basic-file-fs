package none.rg.basicfs;

import java.io.ByteArrayInputStream;
import java.io.File;

/**
 * For now it is going to be used for manual-testing purposes
 * Later we shall make it entry point for utility tool
 */
public class MainTest {

    BasicFs fs;

    public static void main(String... args) {
        new MainTest().run();
    }

    private void run() {
        File f = new File("sample.fs");
        if (f.exists()) {
            f.delete();
        }
        fs = new BasicFs(f.getName());
        fs.createFile("/", "executable.exe", new ByteArrayInputStream(new byte[] {(byte) 0xCD, 0x19}));
        fs.makeDirectory("/", "somedir");
        fs.createFile("/", "file1.txt", new ByteArrayInputStream("Hi, People!\nIt Works!\n".getBytes()));
        fs.createFile("/somedir", "file22.bak", new ByteArrayInputStream(new byte[0]));
        fs.move("/file1.txt", "/somedir");
        fs.move("/somedir/file22.bak", "/");
        fs.rename("/somedir/file1.txt", "text-file.txt");
        System.out.println(new String(readFile("/somedir/text-file.txt")));
        fs.close();
    }

    private byte[] readFile(String path) {
        byte[] buffer = new byte[fs.fileSize(path)];
        fs.startReading(path).read(buffer);
        return buffer;
    }

}
