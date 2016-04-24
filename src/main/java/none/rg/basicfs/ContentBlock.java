package none.rg.basicfs;

public class ContentBlock extends Block {

    private static final int DATA_OFFSET = 8;

    public static final int DATA_SIZE = Block.SIZE - DATA_OFFSET;

    private static final int CONTENT_BLOCK_MARK = 0x80000000;

    public byte[] getContent() {
        byte[] data = new byte[DATA_SIZE];
        getBuffer().position(DATA_OFFSET);
        getBuffer().get(data);
        return data;
    }

    public void setContent(byte[] data, int shift) {
        getBuffer().position(DATA_OFFSET + shift);
        getBuffer().put(data);
    }

    @Override
    public int getBackLink() {
        return super.getBackLink() & ~CONTENT_BLOCK_MARK;
    }

    @Override
    public void setBackLink(int v) {
        super.setBackLink(v | CONTENT_BLOCK_MARK);
    }

    public boolean hasNext() {
        return getNextLink() != Block.ILLEGAL;
    }

    public static boolean isContentBlockMarkSet(Block block) {
        return (block.getBackLink() & CONTENT_BLOCK_MARK) != 0;
    }
}
