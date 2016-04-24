package none.rg.basicfs.operations;

import none.rg.basicfs.Block;
import none.rg.basicfs.BlockStorage;
import none.rg.basicfs.ContentBlock;

public class Reading {

    private final BlockStorage blocks;

    public Reading(BlockStorage blocks) {
        this.blocks = blocks;
    }

    public int readMore(Cursor cursor, byte[] buffer, int start, int length) {
        if (cursor.address == Block.ILLEGAL) {
            return -1;
        }
        int pos = start;
        while (cursor.remains > 0 && length > 0) {
            int bytesRead = readWithinBlock(cursor, buffer, pos, length);
            pos += bytesRead;
            length -= bytesRead;
        }
        return pos - start;
    }

    private int readWithinBlock(Cursor cursor, byte[] buffer, int start, int length) {
        int remains = Integer.min(cursor.remains, ContentBlock.DATA_SIZE - cursor.offset);
        int toRead = Integer.min(remains, length);
        ContentBlock block = blocks.readContent(cursor.address);
        copyIntoArray(buffer, start, block.getContent(), cursor.offset, toRead);
        cursor.offset += toRead;
        if (cursor.offset == ContentBlock.DATA_SIZE) {
            cursor.address = block.getNextLink();
            cursor.offset = 0;
        }
        cursor.remains -= toRead;
        return toRead;
    }

    private void copyIntoArray(byte[] dst, int dstPos, byte[] src, int srcStart, int length) {
        for (int dstEnd = dstPos + length; dstPos < dstEnd;) {
            dst[dstPos++] = src[srcStart++];
        }
    }

    public static class Cursor {

        private int address;
        private int offset;
        private int remains;

        public Cursor(int address, int size) {
            this.address = address;
            offset = 0;
            remains = size;
        }
    }
}
