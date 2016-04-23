package none.rg.basicfs;

import none.rg.basicfs.operations.Creation;
import none.rg.basicfs.operations.Traversing;
import none.rg.basicfs.operations.Writing;
import none.rg.basicfs.storage.FileStorage;
import none.rg.basicfs.storage.PhysicalStorage;

public class BasicFs {

    private BlockStorage blocks;

    private Traversing traversing;
    private Creation creation;
    private Writing writing;

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
        traversing = new Traversing(blocks);
        creation = new Creation(blocks);
        writing = new Writing(blocks);
        if (blocks.size() == 0) {
            creation.createRootDirectory();
        }
    }

    public void close() {
        blocks.close();
    }

    public void makeDirectory(String path, String name) {
        createDirOrFile(path, name, HeaderBlock.Type.DIRECTORY);
    }

    public void createFile(String path, String name) {
        createDirOrFile(path, name, HeaderBlock.Type.FILE);
    }

    private void createDirOrFile(String path, String name, HeaderBlock.Type type) {
        HeaderBlock dir = traversing.findBlock(path);
        HeaderBlock lastEntry = traversing.lastDirEntry(dir);
        creation.createHeader(name, dir, lastEntry, type);
    }

}
