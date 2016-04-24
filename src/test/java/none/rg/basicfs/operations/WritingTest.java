package none.rg.basicfs.operations;

import none.rg.basicfs.Block;
import none.rg.basicfs.BlockStorage;
import none.rg.basicfs.ContentBlock;
import none.rg.basicfs.HeaderBlock;
import none.rg.basicfs.storage.ListStorage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class WritingTest {

    private ListStorage listStorage;

    private BlockStorage blocks;

    private Writing writing;

    private HeaderBlock fileHead;

    private byte[] data;

    @Before
    public void init() {
        listStorage = new ListStorage();
        blocks = new BlockStorage(listStorage);
        writing = new Writing(blocks);
        HeaderBlock root = new HeaderBlock();
        root.setAddress(0);
        blocks.write(root);
        fileHead = new HeaderBlock();
        fileHead.setAddress(1);
        fileHead.setBackLink(0);
        fileHead.setNextLink(Block.ILLEGAL);
        fileHead.setContentLink(Block.ILLEGAL);
        fileHead.setSize(0);
        blocks.write(fileHead);
    }

    @Test
    public void zeroFileWrite() {
        InputStream inputStream = createInput(0);
        writing.appendFile(fileHead, inputStream);
        Assert.assertEquals(2, blocks.size());
        fileHead = blocks.readHeader(1);
        Assert.assertEquals(Block.ILLEGAL, fileHead.getContentLink());
        Assert.assertEquals(0, fileHead.getSize());
    }

    @Test
    public void oneBlockFileWrite() {
        oneBlockFileWrite(100);
    }

    @Test
    public void oneBlockFileWriteWithBoundary() {
        oneBlockFileWrite(ContentBlock.DATA_SIZE);
    }

    private void oneBlockFileWrite(int size) {
        InputStream inputStream = createInput(size);
        writing.appendFile(fileHead, inputStream);
        Assert.assertEquals(3, blocks.size());
        fileHead = blocks.readHeader(1);
        ContentBlock contentBlock = blocks.readContent(2);
        Assert.assertEquals(2, fileHead.getContentLink());
        Assert.assertEquals(data.length, fileHead.getSize());
        Assert.assertEquals(1, contentBlock.getBackLink());
        Assert.assertEquals(Block.ILLEGAL, contentBlock.getNextLink());
    }

    @Test
    public void twoBlockFileWrite() {
        twoBlockFileWrite(ContentBlock.DATA_SIZE + 1);
    }

    @Test
    public void twoBlockFileWriteWithBoundary() {
        twoBlockFileWrite(ContentBlock.DATA_SIZE * 2);
    }

    private void twoBlockFileWrite(int size) {
        InputStream inputStream = createInput(size);
        writing.appendFile(fileHead, inputStream);
        Assert.assertEquals(4, blocks.size());
        fileHead = blocks.readHeader(1);
        ContentBlock contentBlock1 = blocks.readContent(2);
        ContentBlock contentBlock2 = blocks.readContent(3);
        Assert.assertEquals(2, fileHead.getContentLink());
        Assert.assertEquals(data.length, fileHead.getSize());
        Assert.assertEquals(1, contentBlock1.getBackLink());
        Assert.assertEquals(3, contentBlock1.getNextLink());
        Assert.assertEquals(2, contentBlock2.getBackLink());
        Assert.assertEquals(Block.ILLEGAL, contentBlock2.getNextLink());
    }

    private InputStream createInput(int size) {
        data = new byte[size];
        byte cur = 7;
        for (int i = 0; i < size; i++) {
            data[i] = cur;
            cur += 3;
        }
        return new ByteArrayInputStream(data);
    }

}
