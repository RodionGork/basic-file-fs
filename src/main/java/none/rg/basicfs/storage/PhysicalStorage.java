package none.rg.basicfs.storage;

public interface PhysicalStorage {

    void write(int address, byte[] data);
    
    byte[] read(int address);
    
    int size();

    void close();

    void truncate(int size);
}
