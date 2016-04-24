package none.rg.basicfs.storage;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashSet;
import java.util.Set;
import none.rg.basicfs.Block;

public class FileStorage implements PhysicalStorage {

    private static final int WRITE_CACHE_SIZE = 1024;
    
    private RandomAccessFile file;

    private int size;

    private int blockSize;
    
    private Set<Integer> writePending = new HashSet<>();

    public void init(String fileName, int blockSize) {
        try {
            file = new RandomAccessFile(fileName, "rw");
            size = (int) (file.length() / blockSize);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.blockSize = blockSize;
    }

    @Override
    public void close() {
        try {
            file.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(int address, byte[] data) {
        try {
            file.seek(Block.SIZE * (long) address);
            file.write(data);
            writePending.add(address);
            if (writePending.size() >= WRITE_CACHE_SIZE) {
                flush();
            }
            size = Integer.max(size, (int) (file.getFilePointer() / blockSize));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] read(int address) {
        try {
            if (writePending.contains(address)) {
                flush();
            }
            file.seek(Block.SIZE * (long) address);
            byte[] block = new byte[blockSize];
            file.read(block);
            return block;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int size() {
        return size;
    }

    private void flush() throws IOException {
        file.getFD().sync();
        writePending.clear();
    }

}
