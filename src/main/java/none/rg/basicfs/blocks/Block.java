package none.rg.basicfs.blocks;

import java.nio.ByteBuffer;

public class Block {

    public static final int SIZE = 256;

    public static final int ILLEGAL = -1;

    private static final int BACK_LINK_OFFSET = 0;
    private static final int NEXT_LINK_OFFSET = 4;

    private ByteBuffer buffer;

    private int address;

    public Block() {
        buffer = ByteBuffer.allocate(Block.SIZE);
        address = ILLEGAL;
    }

    public void init(byte[] bytes) {
        buffer.position(0);
        buffer.put(bytes);
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public int getBackLink() {
        return buffer.getInt(BACK_LINK_OFFSET);
    }

    public int getNextLink() {
        return buffer.getInt(NEXT_LINK_OFFSET);
    }

    public void setBackLink(int v) {
        buffer.putInt(BACK_LINK_OFFSET, v);
    }

    public void setNextLink(int v) {
        buffer.putInt(NEXT_LINK_OFFSET, v);
    }

    public HeaderBlock asHeaderBlock() {
        return asSpecificBlock(new HeaderBlock());
    }

    public ContentBlock asContentBlock() {
        return asSpecificBlock(new ContentBlock());
    }

    private <T extends Block> T asSpecificBlock(T block) {
        block.getBuffer().put(buffer.array());
        block.setAddress(address);
        return block;
    }

}
