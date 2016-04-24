package none.rg.basicfs;

import java.io.ByteArrayInputStream;

/**
 * For now it is going to be used for manual-testing purposes
 * Later we shall make it entry point for utility tool
 */
public class MainTest {

    public static void main(String... args) {
        BasicFs fs = new BasicFs("sample.fs");
        fs.createFile("/", "file1.txt", new ByteArrayInputStream("Hi, People!\nIt Works!\n".getBytes()));
        fs.makeDirectory("/", "somedir");
        fs.createFile("/somedir", "executable.exe", new ByteArrayInputStream(new byte[] {(byte) 0xCD, 0x19}));
        fs.createFile("/", "file22.bak", new ByteArrayInputStream(new byte[0]));
        fs.close();
    }

}
