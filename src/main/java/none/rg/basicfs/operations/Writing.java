package none.rg.basicfs.operations;

import none.rg.basicfs.BlockStorage;
import none.rg.basicfs.HeaderBlock;

import java.io.InputStream;

public class Writing {

    private BlockStorage blocks;

    public Writing(BlockStorage blocks) {
        this.blocks = blocks;
    }

    public void writeFileContent(HeaderBlock header, InputStream input) {

    }

}
