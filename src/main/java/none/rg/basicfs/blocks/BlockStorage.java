package none.rg.basicfs.blocks;

import none.rg.basicfs.storage.PhysicalStorage;

public class BlockStorage {

    private PhysicalStorage storage;

    public BlockStorage(PhysicalStorage storage) {
        this.storage = storage;
    }

    public void write(Block block) {
        storage.write(block.getAddress(), block.getBuffer().array());
    }

    private <T extends Block> T read(int address, T block) {
        block.init(storage.read(address));
        block.setAddress(address);
        return block;
    }

    public HeaderBlock readHeader(int address) {
        return read(address, new HeaderBlock());
    }

    public ContentBlock readContent(int address) {
        return read(address, new ContentBlock());
    }

    public Block readUnknown(int address) {
        return read(address, new Block());
    }

    public int size() {
        return storage.size();
    }

    public void close() {
        storage.close();
    }

    public void truncate(int size) {
        storage.truncate(size);
    }
}
