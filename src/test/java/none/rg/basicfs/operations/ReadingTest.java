package none.rg.basicfs.operations;

import none.rg.basicfs.blocks.Block;
import none.rg.basicfs.blocks.ContentBlock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

public class ReadingTest extends ListStorageBasedTest {

    private Reading reading;

    private Reading.Cursor cursor;

    @Before
    @Override
    public void init() {
        super.init();
        reading = new Reading(blocks);
    }

    @Test
    public void oneBlockRead() {
        singleReadTest(100, 256);
    }

    @Test
    public void twoBlocksRead() {
        singleReadTest(300, 400);
    }

    private void singleReadTest(int size, int bufSize) {
        prepareData(size);
        byte[] result = new byte[bufSize];
        int bytes = reading.readMore(cursor, result, 0, result.length);
        Assert.assertEquals(data.length, bytes);
        Assert.assertArrayEquals(data, Arrays.copyOfRange(result, 0, bytes));
    }

    @Test
    public void oneBlockReadInParts() {
        twoPartsReadTest(100, 70);
    }

    @Test
    public void twoBlockReadInParts() {
        twoPartsReadTest(256, 200);
    }

    private void twoPartsReadTest(int size, int bufSize) {
        prepareData(size);
        byte[] result = new byte[bufSize];
        int bytes = reading.readMore(cursor, result, 0, result.length);
        Assert.assertEquals(result.length, bytes);
        Assert.assertArrayEquals(Arrays.copyOfRange(data, 0, bytes), result);
        bytes = reading.readMore(cursor, result, 0, result.length);
        Assert.assertEquals(data.length - result.length, bytes);
        Assert.assertArrayEquals(Arrays.copyOfRange(data, result.length, result.length + bytes),
                Arrays.copyOfRange(result, 0, bytes));
    }

    private void prepareData(int size) {
        createInput(size);
        fileHead.setContentLink(size > 0 ? blocks.size() : Block.ILLEGAL);
        fileHead.setSize(size);
        blocks.write(fileHead);
        for (int offset = 0; offset < size; offset += ContentBlock.DATA_SIZE) {
            int toWrite = Integer.min(size - offset, ContentBlock.DATA_SIZE);
            ContentBlock block = new ContentBlock();
            block.setAddress(blocks.size());
            block.setNextLink(offset + toWrite < size ? block.getAddress() + 1 : Block.ILLEGAL);
            block.setContent(Arrays.copyOfRange(data, offset, offset + toWrite), 0);
            blocks.write(block);
        }
        cursor = new Reading.Cursor(fileHead.getContentLink(), fileHead.getSize());
    }

}
