package none.rg.basicfs.storage;

import none.rg.basicfs.Block;

import java.io.IOException;
import java.io.RandomAccessFile;

public class FileStorage implements PhysicalStorage {

    private RandomAccessFile file;

    private int size;

    private int blockSize;

    public void init(String fileName, int blockSize) {
        try {
            file = new RandomAccessFile(fileName, "rws");
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
            size = Integer.max(size, (int) (file.getFilePointer() / blockSize));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] read(int address) {
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

}
