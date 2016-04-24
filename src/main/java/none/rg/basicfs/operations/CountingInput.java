package none.rg.basicfs.operations;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class CountingInput {

    private InputStream input;

    private int bytesRead;

    public CountingInput(InputStream input) {
        this.input = input;
        bytesRead = 0;
    }

    public byte[] read(int max) {
        byte[] buffer = new byte[max];
        int size;
        try {
            size = input.read(buffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (size < 0) {
            size = 0;
        }
        bytesRead += size;
        return size == max ? buffer : Arrays.copyOfRange(buffer, 0, size);
    }

    public int getBytesRead() {
        return bytesRead;
    }

}
