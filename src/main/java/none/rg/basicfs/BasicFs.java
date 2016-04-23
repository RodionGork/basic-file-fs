package none.rg.basicfs;

import none.rg.basicfs.storage.FileStorage;
import none.rg.basicfs.storage.PhysicalStorage;

public class BasicFs {

    private BlockStorage blocks;

    public BasicFs(String fileName) {
        FileStorage storage = new FileStorage();
        storage.init(fileName, Block.SIZE);
        init(storage);
    }

    public BasicFs(PhysicalStorage storage) {
        init(storage);
    }

    private void init(PhysicalStorage storage) {
        blocks = new BlockStorage(storage);
        if (blocks.size() == 0) {
            createRootDirectory();
        }
    }

    public void close() {
        blocks.close();
    }

    public void makeDirectory(String path, String name) {
        int dirAddress = findBlock(path);
        createHeader(name, dirAddress, HeaderBlock.Type.DIRECTORY);
    }

    public void createFile(String path, String name) {
        int dirAddress = findBlock(path);
        createHeader(name, dirAddress, HeaderBlock.Type.FILE);
    }

    // trash below should go into dedicated classes later, I think

    private void createRootDirectory() {
        writeHeaderBlock("", 0, Block.ILLEGAL, HeaderBlock.Type.DIRECTORY);
    }

    private HeaderBlock createHeader(String name, int dirAddress, HeaderBlock.Type type) {
        HeaderBlock dir = blocks.readHeader(dirAddress);
        int newAddress = blocks.size();
        int backLink;
        if (dir.isEmpty()) {
            backLink = dirAddress;
            dir.setContentLink(newAddress);
            blocks.write(dir);
        } else {
            HeaderBlock prevSibling = lastDirEntry(dir);
            prevSibling.setNextLink(newAddress);
            blocks.write(prevSibling);
            backLink = prevSibling.getAddress();
        }
        return writeHeaderBlock(name, newAddress, backLink, type);
    }

    private HeaderBlock lastDirEntry(HeaderBlock dir) {
        int currentAddress = dir.getContentLink();
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

    private String[] splitPath(String name) {
        return name.split("\\/");
    }

    private int findBlock(int from, String name) {
        if (name.isEmpty()) {
            return from;
        }
        HeaderBlock dir = blocks.readHeader(from);
        int next = dir.getContentLink();
        while (next >= 0) {
            HeaderBlock block = blocks.readHeader(next);
            if (name.equals(block.getName())) {
                return next;
            }
            next = block.getNextLink();
        }
        return -1;
    }

    private int findBlock(String path) {
        String[] segments = splitPath(path);
        int cur = 0;
        for (String name : segments) {
            cur = findBlock(cur, name);
        }
        return cur;
    }

}
