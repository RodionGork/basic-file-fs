package none.rg.basicfs;

import none.rg.basicfs.operations.Traversing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FsUtils {

    private static final int FILE_COPY_BUFFER_SIZE = 1024;

    private BasicFs fs;
    private Traversing traversing;

    public FsUtils(BasicFs fs, Traversing traversing) {
        this.fs = fs;
        this.traversing = traversing;
    }

    public int put(String path, String physicalPath) {
        File physicalFile = new File(physicalPath);
        if (!physicalFile.exists()) {
            return 0;
        }
        String name = physicalFile.getName();
        if (physicalFile.isFile()) {
            try (FileInputStream input = new FileInputStream(physicalFile)) {
                fs.createFile(path, name, input);
            } catch (IOException e) {
                return 0;
            }
            return 1;
        }
        if (physicalFile.isDirectory()) {
            fs.makeDirectory(path, name);
            int sum = 1;
            for (File child : physicalFile.listFiles()) {
                sum += put(path + "/" + name, child.getAbsolutePath());
            }
            return sum;
        }
        return 0;
    }

    public int get(String path, String physicalPath) {
        HeaderBlock block = traversing.findBlock(path);
        File physicalFile = new File(physicalPath);
        if (block == null || block.isRoot() || !physicalFile.isDirectory()) {
            return 0;
        }
        File newFile = new File(physicalFile, block.getName());
        if (!block.isDirectory()) {
            try (FileOutputStream output = new FileOutputStream(newFile)) {
                getFile(path, output);
            } catch (Exception e) {
                return 0;
            }
            return 1;
        }
        newFile.mkdir();
        int sum = 1;
        for (String child : fs.list(path)) {
            sum += get(path + "/" + child, newFile.getAbsolutePath());
        }
        return sum;
    }

    private void getFile(String path, FileOutputStream output) throws IOException {
        BasicFs.ReadingHandle handle = fs.startReading(path);
        byte[] buffer = new byte[FILE_COPY_BUFFER_SIZE];
        while (true) {
            int bytes = handle.read(buffer);
            if (bytes < 1) {
                break;
            }
            output.write(buffer, 0, bytes);
        }
    }

    public int deleteTree(String path) {
        HeaderBlock block = traversing.findBlock(path);
        if (block == null || block.isRoot()) {
            return 0;
        }
        int sum = 1;
        if (block.isDirectory()) {
            for (String child : fs.list(path)) {
                sum += deleteTree(path + "/" + child);
            }
        }
        fs.delete(path);
        return sum;
    }
}
