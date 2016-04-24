package none.rg.basicfs.operations;

import none.rg.basicfs.Block;
import none.rg.basicfs.BlockStorage;
import none.rg.basicfs.HeaderBlock;
import none.rg.basicfs.storage.ListStorage;
import org.junit.Before;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class ListStorageBasedTest {

    protected ListStorage listStorage;

    protected BlockStorage blocks;

    protected HeaderBlock fileHead;

    protected byte[] data;

    @Before
    public void init() {
        listStorage = new ListStorage();
        blocks = new BlockStorage(listStorage);
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

    protected InputStream createInput(int size) {
        data = new byte[size];
        byte cur = 7;
        for (int i = 0; i < size; i++) {
            data[i] = cur;
            cur += 3;
        }
        return new ByteArrayInputStream(data);
    }
}
