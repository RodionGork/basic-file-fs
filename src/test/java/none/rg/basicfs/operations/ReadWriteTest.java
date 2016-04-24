package none.rg.basicfs.operations;

import none.rg.basicfs.Block;
import none.rg.basicfs.ContentBlock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@RunWith(Parameterized.class)
public class ReadWriteTest extends ListStorageBasedTest {

    private Reading reading;
    private Writing writing;

    private int[] dataSizes;
    private int totalSize;

    @Parameterized.Parameters
    public static Collection<Object[]> parameters() {
        int[][] data = new int[][] {
                {100},
                {ContentBlock.DATA_SIZE},
                {ContentBlock.DATA_SIZE + 1},
                {ContentBlock.DATA_SIZE * 5},
                {ContentBlock.DATA_SIZE * 5 + 1},
                {100, 100},
                {100, 124},
                {100, 124, 124, 100},
                {100, 125, 123, 100, 100},
                {100, 123, 125, 100, 100},
        };
        return Arrays.asList(data).stream().map(x -> new Object[] {x}).collect(Collectors.<Object[]>toList());
    }

    public ReadWriteTest(int[] sizes) {
        dataSizes = sizes;
        totalSize = 0;
        for (int size : dataSizes) {
            totalSize += size;
        }
    }

    @Before
    @Override
    public void init() {
        super.init();
        reading = new Reading(blocks);
        writing = new Writing(blocks);

        createInput(totalSize);
        writeAppending();
    }

    @Test
    public void checkLinksAfterWrite() {
        int totalBlocks = (totalSize + ContentBlock.DATA_SIZE - 1) / ContentBlock.DATA_SIZE;
        ContentBlock block = null;
        for (int i = 0, next = fileHead.getContentLink(); i < totalBlocks; i++) {
            Assert.assertEquals(i + 2, next);
            block = blocks.readContent(next);
            next = block.getNextLink();
        }
        Assert.assertEquals(Block.ILLEGAL, block.getNextLink());
        for (int i = totalBlocks - 1; i >= 0; i--) {
            Assert.assertEquals(i + 1, block.getBackLink());
            block = blocks.readContent(block.getBackLink());
        }

    }

    @Test
    public void readFull() {
        byte[] result = new byte[totalSize];
        Reading.Cursor cursor = new Reading.Cursor(fileHead.getContentLink(), totalSize);
        reading.readMore(cursor, result, 0, totalSize);
        Assert.assertArrayEquals(data, result);
    }

    @Test
    public void readUnevenSegments() {
        checkReadInSegments(totalSize, 91);
    }

    @Test
    public void readOnBoundaries() {
        checkReadInSegments(totalSize, ContentBlock.DATA_SIZE / 2);
    }

    private void checkReadInSegments(int size, int segSize) {
        byte[] result = new byte[size];
        byte[] segment = new byte[segSize];
        Reading.Cursor cursor = new Reading.Cursor(fileHead.getContentLink(), size);
        int offset = 0;
        while (true) {
            int bytes = reading.readMore(cursor, segment, 0, segment.length);
            if (bytes <= 0) {
                break;
            }
            for (int i = 0; i < bytes; i++) {
                result[offset++] = segment[i];
            }
        }
        Assert.assertArrayEquals(data, result);
    }

    private void writeAppending() {
        int offset = 0;
        for (int size : dataSizes) {
            writing.appendFile(fileHead, new ByteArrayInputStream(Arrays.copyOfRange(data, offset, offset + size)));
            offset += size;
        }
    }

}
