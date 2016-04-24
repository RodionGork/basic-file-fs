package none.rg.basicfs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BasicFsTest {
    
    private static final String TEMP_FILE_NAME = "temp.fs";
    private static final int TEST_FILE_SIZE = 3_000_000;
    private static final double MEGABYTE = Math.pow(1024, 2);
    
    private BasicFs fs;
    
    @Before
    public void init() {
        fs = new BasicFs(TEMP_FILE_NAME);
    }
    
    @After
    public void cleanUp() {
        fs.close();
        new File(TEMP_FILE_NAME).delete();
    }
    
    @Test
    public void readAndWriteSpeedTest() throws IOException {
        long time = System.currentTimeMillis();
        try (InputStream input = new PipedInputStream(createSource())) {
            fs.createFile("/", "test.bin", input);
        }
        time = System.currentTimeMillis() - time;
        Assert.assertEquals(TEST_FILE_SIZE, fs.fileSize("/test.bin"));
        double writeSpeed = TEST_FILE_SIZE / MEGABYTE / (time / 1000.0);
        System.out.printf("Write speed: %.1f Mb/sec%n", writeSpeed);
        time = System.currentTimeMillis();
        int bytesRead = performReading();
        time = System.currentTimeMillis() - time;
        Assert.assertEquals(TEST_FILE_SIZE, bytesRead);
        double readSpeed = TEST_FILE_SIZE / MEGABYTE / (time / 1000.0);
        System.out.printf("Read speed: %.1f Mb/sec%n", readSpeed);
    }

    private int performReading() {
        BasicFs.ReadingHandle handle = fs.startReading("/test.bin");
        byte[] buffer = new byte[1024];
        int bytesRead = 0;
        while (true) {
            int size = handle.read(buffer);
            if (size < 1) {
                break;
            }
            bytesRead += size;
        }
        return bytesRead;
    }

    private PipedOutputStream createSource() {
        PipedOutputStream source = new PipedOutputStream();
        new Thread(() -> {
            try {
                byte[] data = new byte[256];
                for (int i = 0; i < data.length; i++) {
                    data[i] = (byte) (7 + i * 3);
                }
                for (int i = 0; i < TEST_FILE_SIZE; i += data.length) {
                    source.write(data, 0, Integer.min(data.length, TEST_FILE_SIZE - i));
                }
                source.close();
            } catch (IOException e) {
                throw new RuntimeException();
            }
        }).start();
        return source;
    }
    
}
