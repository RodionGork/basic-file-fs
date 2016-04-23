package none.rg.basicfs.operations;

import none.rg.basicfs.Block;
import none.rg.basicfs.BlockStorage;
import none.rg.basicfs.HeaderBlock;

public class Traversing {

    private BlockStorage blocks;

    public Traversing(BlockStorage blocks) {
        this.blocks = blocks;
    }

    private String[] splitPath(String name) {
        return name.split("\\/");
    }

    private HeaderBlock findBlock(HeaderBlock dir, String name) {
        if (name.isEmpty()) {
            return dir;
        }
        int next = dir.getContentLink();
        while (next >= 0) {
            HeaderBlock block = blocks.readHeader(next);
            if (name.equals(block.getName())) {
                return block;
            }
            next = block.getNextLink();
        }
        return null;
    }

    public HeaderBlock findBlock(String path) {
        String[] segments = splitPath(path);
        HeaderBlock cur  = blocks.readHeader(0);
        for (String name : segments) {
            cur = findBlock(cur, name);
        }
        return cur;
    }

    public HeaderBlock lastDirEntry(HeaderBlock dir) {
        int currentAddress = dir.getContentLink();
        if (currentAddress == Block.ILLEGAL) {
            return null;
        }
        HeaderBlock entry;
        while (true) {
            entry = blocks.readHeader(currentAddress);
            int next = entry.getNextLink();
            if (next < 0) {
                return entry;
            }
            currentAddress = next;
        }
    }

}