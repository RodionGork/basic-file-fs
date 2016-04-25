package none.rg.basicfs.storage;

import none.rg.basicfs.blocks.Block;
import none.rg.basicfs.blocks.ContentBlock;

import java.util.ArrayList;
import java.util.List;

public class ListStorage implements PhysicalStorage {

    private List<Block> list = new ArrayList<>();

    @Override
    public void write(int address, byte[] data) {
        Block block = new Block();
        block.init(data);
        block.setAddress(address);
        block = ContentBlock.isContentBlockMarkSet(block) ? block.asContentBlock() : block.asHeaderBlock();
        if (address < list.size()) {
            list.set(address, block);
        } else if (address == list.size()) {
            list.add(block);
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public byte[] read(int address) {
        return list.get(address).getBuffer().array();
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public void close() {
    }

    @Override
    public void truncate(int size) {
        while (list.size() > size) {
            list.remove(list.size() - 1);
        }
    }
}
