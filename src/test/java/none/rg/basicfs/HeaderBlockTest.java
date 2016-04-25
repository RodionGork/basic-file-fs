package none.rg.basicfs;

import none.rg.basicfs.blocks.HeaderBlock;
import org.junit.Assert;

public class HeaderBlockTest {

    public void testSetBlock() {
        byte[] expected = {
                0x10, 0x20, 0x30, 0x40, 0x11, 0x21, 0x31, 0x41, 0x12, 0x22, 0x32, 0x42, 0x13, 0x23, 0x33, 0x43,
                0x04, (byte) 'n', (byte) 'a', (byte) 'm', (byte) 'e', 0x00
        };
        HeaderBlock block = new HeaderBlock();
        block.setBackLink(0x10203040);
        block.setNextLink(0x11213141);
        block.setContentLink(0x12223242);
        block.setSize(0x13233343);
        block.setName("name");
        byte[] content = new byte[22];
        block.getBuffer().get(content);
        Assert.assertArrayEquals(expected, content);
    }

}
