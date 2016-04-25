package none.rg.basicfs.operations;

import java.io.InputStream;
import none.rg.basicfs.blocks.Block;
import none.rg.basicfs.blocks.BlockStorage;
import none.rg.basicfs.blocks.ContentBlock;
import none.rg.basicfs.blocks.HeaderBlock;
import none.rg.basicfs.exception.WrongEntryTypeException;

public class Writing {

    private BlockStorage blocks;

    public Writing(BlockStorage blocks) {
        this.blocks = blocks;
    }

    public void appendFile(HeaderBlock header, InputStream inputStream) {
        WrongEntryTypeException.check(header, HeaderBlock.Type.FILE);
        CountingInput input = new CountingInput(inputStream);
        ContentBlock lastBlock = lastContentBlock(header);
        amendExistingTail(input, header, lastBlock);
        int newAddress = blocks.size();
        writeChain(input, header, lastBlock);
        updateHeader(header, input.getBytesRead(), newAddress);
    }

    private void amendExistingTail(CountingInput input, HeaderBlock header, ContentBlock lastBlock) {
        int tail = header.getSize() % ContentBlock.DATA_SIZE;
        if (tail > 0) {
            byte[] amend = input.read(ContentBlock.DATA_SIZE - tail);
            lastBlock.setContent(amend, tail);
        }
    }

    private ContentBlock lastContentBlock(HeaderBlock header) {
        if (header.isEmpty()) {
            return null;
        }
        ContentBlock current = blocks.readContent(header.getContentLink());
        while (current.hasNext()) {
            current = blocks.readContent(current.getNextLink());
        }
        return current;
    }

    private void writeChain(CountingInput input, HeaderBlock headerBlock, ContentBlock lastBlock) {
        int newAddress = blocks.size();
        while (true) {
            byte[] data = input.read(ContentBlock.DATA_SIZE);
            if (data.length == 0) {
                if (lastBlock != null) {
                    lastBlock.setNextLink(Block.ILLEGAL);
                    blocks.write(lastBlock);
                }
                break;
            }
            ContentBlock newBlock = new ContentBlock();
            if (lastBlock != null) {
                lastBlock.setNextLink(newAddress);
                blocks.write(lastBlock);
                newBlock.setBackLink(lastBlock.getAddress());
            } else {
                newBlock.setBackLink(headerBlock.getAddress());
            }
            newBlock.setAddress(newAddress);
            newBlock.setContent(data, 0);
            lastBlock = newBlock;
            newAddress++;
        }
    }

    private void updateHeader(HeaderBlock header, int byteCount, int newAddress) {
        if (byteCount > 0) {
            if (header.isEmpty()) {
                header.setContentLink(newAddress);
            }
            header.setSize(header.getSize() + byteCount);
            blocks.write(header);
        }
    }

}
