package none.rg.basicfs;

public class HeaderBlock extends Block {

    private static final int CONTENT_LINK_OFFSET = 8;
    private static final int SIZE_OFFSET = 12;
    private static final int NAME_LENGTH_OFFSET = 16;
    private static final int NAME_OFFSET = 17;
    private static final int BYTE_MASK = 0xFF;

    public enum Type {
        DIRECTORY, FILE
    }

    public int getContentLink() {
        return getBuffer().getInt(CONTENT_LINK_OFFSET);
    }

    public int getSize() {
        return getBuffer().getInt(SIZE_OFFSET);
    }

    public String getName() {
        int size = getBuffer().get(NAME_LENGTH_OFFSET) & BYTE_MASK;
        byte[] bytes = new byte[size];
        getBuffer().position(NAME_OFFSET);
        getBuffer().get(bytes);
        return new String(bytes);
    }

    public void setContentLink(int v) {
        getBuffer().putInt(CONTENT_LINK_OFFSET, v);
    }

    public void setSize(int v) {
        getBuffer().putInt(SIZE_OFFSET, v);
    }

    public void setName(String s) {
        byte[] bytes = s.getBytes();
        getBuffer().put(NAME_LENGTH_OFFSET, (byte) bytes.length);
        getBuffer().position(NAME_OFFSET);
        getBuffer().put(bytes);
    }

    public Type getType() {
        return getSize() == Block.ILLEGAL ? Type.DIRECTORY : Type.FILE;
    }

    public boolean isEmpty() {
        return getContentLink() == Block.ILLEGAL;
    }
    
    public boolean isDirectory() {
        return getType() == Type.DIRECTORY;
    }
    
    public boolean isRoot() {
        return getBackLink() == Block.ILLEGAL;
    }

}
