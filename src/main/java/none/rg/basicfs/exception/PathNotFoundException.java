package none.rg.basicfs.exception;

public class PathNotFoundException extends BasicFsException {

    public PathNotFoundException(String path) {
        super("Path could not be found: " + path);
    }

}
