package none.rg.basicfs.storage;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import none.rg.basicfs.blocks.Block;

public class FileStorage implements PhysicalStorage {

    private static final int CACHE_SIZE = 1024;
    
    private RandomAccessFile file;

    private int size;

    private int blockSize;
    
    private Map<Integer, byte[]> cache = new HashMap<>();

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
            cache.put(address, Arrays.copyOf(data, Block.SIZE));
            if (cache.size() >= CACHE_SIZE) {
                flush();
            }
            size = Integer.max(size, (int) (file.getFilePointer() / blockSize));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] read(int address) {
        byte[] fromCache = cache.get(address);
        if (fromCache != null) {
            return Arrays.copyOf(fromCache, Block.SIZE);
        }
        try {
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

    @Override
    public void truncate(int size) {
        this.size = size;
        try {
            file.setLength(Block.SIZE * (long) size);
            flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void flush() throws IOException {
        file.getFD().sync();
        cache.clear();
    }

}
