package none.rg.basicfs.operations;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class CountingInputTest {

    @Test
    public void testCountingInput() {
        byte[] testData = "test-data".getBytes();
        InputStream is = new ByteArrayInputStream(testData);
        CountingInput input = new CountingInput(is);
        byte[] chunk1 = input.read(100);
        byte[] chunk2 = input.read(100);
        Assert.assertArrayEquals(testData, chunk1);
        Assert.assertEquals(0, chunk2.length);
        Assert.assertEquals(testData.length, input.getBytesRead());
    }

}
