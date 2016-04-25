package none.rg.basicfs.operations;

import none.rg.basicfs.blocks.Block;
import none.rg.basicfs.blocks.BlockStorage;
import none.rg.basicfs.blocks.ContentBlock;
import none.rg.basicfs.blocks.HeaderBlock;
import none.rg.basicfs.exception.BasicFsException;

public class Deleting {

    private BlockStorage blocks;
    private TreeOperations treeOperations;

    public Deleting(BlockStorage blocks, TreeOperations treeOperations) {
        this.blocks = blocks;
        this.treeOperations = treeOperations;
    }

    public void eraseFileContent(HeaderBlock block, DeletionCounters counters) {
        int headerBlockAddress = block.getAddress();
        int currentAddress = block.getContentLink();
        block.setContentLink(Block.ILLEGAL);
        block.setSize(0);
        blocks.write(block);
        while (currentAddress != Block.ILLEGAL) {
            int next = blocks.readContent(currentAddress).getNextLink();
            counters.size -= 1;
            moveBlock(counters.size, currentAddress);
            if (headerBlockAddress == counters.size) {
                headerBlockAddress = currentAddress;
            }
            if (next != counters.size) {
                currentAddress = next;
            }
        }
    }

    private void moveBlock(int from, int to) {
        if (from == to) {
            return;
        }
        Block block = blocks.readUnknown(from);
        if (ContentBlock.isContentBlockMarkSet(block)) {
            updateContentBlockNeighbors(block.asContentBlock(), to);
        } else {
            updateHeaderBlockNeighbors(block.asHeaderBlock(), to);
        }
        block.setAddress(to);
        blocks.write(block);
    }

    private void updateHeaderBlockNeighbors(HeaderBlock block, int newAddress) {
        updateBackLink(block.getNextLink(), newAddress);
        updateBackLink(block.getContentLink(), newAddress);
        HeaderBlock prev = blocks.readHeader(block.getBackLink());
        if (prev.getContentLink() == block.getAddress()) {
            prev.setContentLink(newAddress);
        } else {
            prev.setNextLink(newAddress);
        }
        blocks.write(prev);
    }

    private void updateContentBlockNeighbors(ContentBlock block, int newAddress) {
        updateBackLink(block.getNextLink(), newAddress);
        Block prev = blocks.readUnknown(block.getBackLink());
        if (ContentBlock.isContentBlockMarkSet(prev)) {
            prev.setNextLink(newAddress);
        } else {
            prev = prev.asHeaderBlock();
            ((HeaderBlock) prev).setContentLink(newAddress);
        }
        blocks.write(prev);
    }

    private void updateBackLink(int address, int backLink) {
        if (address == Block.ILLEGAL) {
            return;
        }
        Block block = blocks.readUnknown(address);
        block = ContentBlock.isContentBlockMarkSet(block)
                ? block.asContentBlock() : block.asHeaderBlock();
        block.setBackLink(backLink);
        blocks.write(block);
    }

    public void eraseDirectoryEntry(HeaderBlock block, DeletionCounters counters, boolean truncate) {
        if (block.isRoot()) {
            throw new BasicFsException("Root directory could not be deleted");
        }
        treeOperations.detachDirectoryEntry(block);
        counters.size -= 1;
        moveBlock(counters.size, block.getAddress());
        counters.entries += 1;
        if (truncate) {
            blocks.truncate(counters.size);
        }
    }

    public static class DeletionCounters {

        private int size;
        private int entries;

        public DeletionCounters(int size) {
            this.size = size;
            this.entries = 0;
        }

        public int getEntries() {
            return entries;
        }
    }

}
