package none.rg.basicfs.operations;

import none.rg.basicfs.Block;
import none.rg.basicfs.BlockStorage;
import none.rg.basicfs.ContentBlock;
import none.rg.basicfs.HeaderBlock;
import none.rg.basicfs.exception.BasicFsException;

public class Deleting {

    private BlockStorage blocks;
    private TreeOperations treeOperations;

    public Deleting(BlockStorage blocks, TreeOperations treeOperations) {
        this.blocks = blocks;
        this.treeOperations = treeOperations;
    }

    public void eraseFileContent(HeaderBlock block) {
        int currentAddress = block.getContentLink();
        int lastBlockAddress = blocks.size();
        while (currentAddress != Block.ILLEGAL) {
            int next = blocks.readContent(currentAddress).getNextLink();
            lastBlockAddress -= 1;
            moveBlock(lastBlockAddress, currentAddress);
            currentAddress = next;
        }
        block.setContentLink(Block.ILLEGAL);
        block.setSize(0);
        blocks.write(block);
        blocks.truncate(lastBlockAddress);
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

    public void eraseDirectoryEntry(HeaderBlock block) {
        if (block.isRoot()) {
            throw new BasicFsException("Root directory could not be deleted");
        }
        treeOperations.detachDirectoryEntry(block);
        int lastBlockAddress = blocks.size() - 1;
        moveBlock(lastBlockAddress, block.getAddress());
        blocks.truncate(lastBlockAddress);
    }

}
