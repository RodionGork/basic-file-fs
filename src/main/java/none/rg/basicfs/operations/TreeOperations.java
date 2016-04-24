package none.rg.basicfs.operations;

import none.rg.basicfs.Block;
import none.rg.basicfs.BlockStorage;
import none.rg.basicfs.HeaderBlock;
import none.rg.basicfs.exception.BasicFsException;
import none.rg.basicfs.exception.WrongEntryTypeException;

public class TreeOperations {

    private BlockStorage blocks;
    private Traversing traversing;

    public TreeOperations(BlockStorage blocks, Traversing traversing) {
        this.blocks = blocks;
        this.traversing = traversing;
    }

    public void createRootDirectory() {
        HeaderBlock root = initDirectoryEntry(0, "", HeaderBlock.Type.DIRECTORY);
        root.setBackLink(Block.ILLEGAL);
        blocks.write(root);
    }

    public HeaderBlock createDirectoryEntry(HeaderBlock dir, String name, HeaderBlock.Type type) {
        HeaderBlock lastEntry = traversing.lastDirEntry(dir, name);
        int newAddress = blocks.size();
        HeaderBlock block = initDirectoryEntry(newAddress, name, type);
        attachInTail(block, dir, lastEntry);
        return block;
    }

    private HeaderBlock initDirectoryEntry(int address, String name, HeaderBlock.Type type) {
        HeaderBlock block = new HeaderBlock();
        block.setContentLink(Block.ILLEGAL);
        block.setNextLink(Block.ILLEGAL);
        block.setSize(type == HeaderBlock.Type.DIRECTORY ? Block.ILLEGAL : 0);
        block.setName(name);
        block.setAddress(address);
        return block;
    }

    public void rename(HeaderBlock block, String name) {
        if (block.isRoot()) {
            throw new BasicFsException("Root directory could not be renamed");
        }
        block.setName(name);
        blocks.write(block);
    }

    public void move(HeaderBlock block, HeaderBlock dir) {
        WrongEntryTypeException.check(dir, HeaderBlock.Type.DIRECTORY);
        if (block.isRoot()) {
            throw new BasicFsException("Root directory could not be moved");
        }
        HeaderBlock lastEntry = traversing.lastDirEntry(dir, block.getName());
        detachDirectoryEntry(block);
        dir = blocks.readHeader(dir.getAddress());
        if (lastEntry != null) {
            lastEntry = blocks.readHeader(lastEntry.getAddress());
        }
        attachInTail(block, dir, lastEntry);
    }

    private void attachInTail(HeaderBlock block, HeaderBlock dir, HeaderBlock lastEntry) {
        if (lastEntry != null) {
            lastEntry.setNextLink(block.getAddress());
            block.setBackLink(lastEntry.getAddress());
            blocks.write(lastEntry);
        } else {
            dir.setContentLink(block.getAddress());
            block.setBackLink(dir.getAddress());
            blocks.write(dir);
        }
        block.setNextLink(Block.ILLEGAL);
        blocks.write(block);
    }

    public void detachDirectoryEntry(HeaderBlock block) {
        int prev = block.getBackLink();
        HeaderBlock prevBlock = blocks.readHeader(prev);
        int next = block.getNextLink();
        if (prevBlock.isDirectory() && prevBlock.getContentLink() == block.getAddress()) {
            prevBlock.setContentLink(next);
        } else {
            prevBlock.setNextLink(next);
        }
        blocks.write(prevBlock);
        if (next != Block.ILLEGAL) {
            HeaderBlock nextBlock = blocks.readHeader(next);
            nextBlock.setBackLink(prev);
            blocks.write(nextBlock);
        }
    }

}
