package none.rg.basicfs.operations;

import none.rg.basicfs.Block;
import none.rg.basicfs.BlockStorage;
import none.rg.basicfs.HeaderBlock;
import none.rg.basicfs.exception.EntryExistsException;

public class Creation {

    private BlockStorage blocks;

    public Creation(BlockStorage blocks) {
        this.blocks = blocks;
    }

    public void createRootDirectory() {
        writeHeaderBlock("", 0, Block.ILLEGAL, HeaderBlock.Type.DIRECTORY);
    }

    public HeaderBlock createHeader(String name, HeaderBlock dir, HeaderBlock last, HeaderBlock.Type type) {
        int newAddress = blocks.size();
        HeaderBlock prev = updatePrevious(dir, last, newAddress);
        return writeHeaderBlock(name, newAddress, prev.getAddress(), type);
    }

    private HeaderBlock updatePrevious(HeaderBlock dir, HeaderBlock last, int newAddress) {
        HeaderBlock prev;
        if (last == null) {
            prev = dir;
            prev.setContentLink(newAddress);
        } else {
            prev = last;
            prev.setNextLink(newAddress);
        }
        blocks.write(prev);
        return prev;
    }

    private HeaderBlock writeHeaderBlock(String name, int address, int backLink, HeaderBlock.Type type) {
        HeaderBlock block = new HeaderBlock();
        block.setBackLink(backLink);
        block.setNextLink(Block.ILLEGAL);
        block.setContentLink(Block.ILLEGAL);
        block.setSize(type == HeaderBlock.Type.DIRECTORY ? Block.ILLEGAL : 0);
        block.setName(name);
        block.setAddress(address);
        blocks.write(block);
        return block;
    }

    public HeaderBlock createDirectoryEntry(HeaderBlock dir, String name, HeaderBlock.Type type) {
        HeaderBlock lastEntry = lastDirEntry(dir, name);
        return createHeader(name, dir, lastEntry, type);
    }
    
    public HeaderBlock lastDirEntry(HeaderBlock dir, String name) {
        int currentAddress = dir.getContentLink();
        if (currentAddress == Block.ILLEGAL) {
            return null;
        }
        HeaderBlock entry;
        while (true) {
            entry = blocks.readHeader(currentAddress);
            if (entry.getName().equals(name)) {
                throw new EntryExistsException(name);
            }
            int next = entry.getNextLink();
            if (next < 0) {
                return entry;
            }
            currentAddress = next;
        }
    }

}
