package none.rg.basicfs.exception;

public class EntryExistsException extends BasicFsException {

    public EntryExistsException(String entry) {
        super("Entry already exists: " + entry);
    }

}
