package none.rg.basicfs;

import none.rg.basicfs.exception.PathNotFoundException;
import none.rg.basicfs.operations.Creation;
import none.rg.basicfs.operations.Reading;
import none.rg.basicfs.operations.Traversing;
import none.rg.basicfs.operations.Writing;
import none.rg.basicfs.storage.FileStorage;
import none.rg.basicfs.storage.PhysicalStorage;

import java.io.InputStream;

public class BasicFs {

    private BlockStorage blocks;

    private Traversing traversing;
    private Creation creation;
    private Writing writing;
    private Reading reading;

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
        reading = new Reading(blocks);
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

    public void createFile(String path, String name, InputStream input) {
        HeaderBlock fileHead = createDirOrFile(path, name, HeaderBlock.Type.FILE);
        writing.appendFile(fileHead, input);
    }

    private HeaderBlock createDirOrFile(String path, String name, HeaderBlock.Type type) {
        HeaderBlock dir = findBlockOrError(path);
        return creation.createDirectoryEntry(dir, name, type);
    }

    public int fileSize(String path) {
        return findBlockOrError(path).getSize();
    }

    public ReadingHandle startReading(String path) {
        HeaderBlock file = findBlockOrError(path);
        return new ReadingHandle(file.getContentLink(), file.getSize());
    }

    private HeaderBlock findBlockOrError(String path) {
        HeaderBlock block = traversing.findBlock(path);
        if (block == null) {
            throw new PathNotFoundException(path);
        }
        return block;
    }

    public class ReadingHandle {

        private Reading.Cursor cursor;

        public ReadingHandle(int address, int size) {
            cursor = new Reading.Cursor(address, size);
        }

        public int read(byte[] buffer) {
            return read(buffer, 0, buffer.length);
        }

        public int read(byte[] buffer, int start, int length) {
            return reading.readMore(cursor, buffer, start, length);
        }


    }

}
