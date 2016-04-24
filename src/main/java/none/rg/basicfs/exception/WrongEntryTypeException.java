package none.rg.basicfs.exception;

import none.rg.basicfs.HeaderBlock;

public class WrongEntryTypeException extends BasicFsException {
    
    public WrongEntryTypeException(HeaderBlock.Type type) {
        super("Wrong entry type for requested operation: " + type);
    }
    
    public static void check(HeaderBlock block, HeaderBlock.Type type) {
        if (block.getType() != type) {
            throw new WrongEntryTypeException(block.getType());
        }
    }
    
}
