package none.rg.basicfs.operations;

import none.rg.basicfs.blocks.Block;
import none.rg.basicfs.blocks.ContentBlock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;

public class WritingTest extends ListStorageBasedTest {

    protected Writing writing;

    @Before
    @Override
    public void init() {
        super.init();
        writing = new Writing(blocks);
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

}
