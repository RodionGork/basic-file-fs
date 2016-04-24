package none.rg.basicfs;

public class PathNotFoundException extends RuntimeException {

    public PathNotFoundException(String path) {
        super("Path could not be found: " + path);
    }

}
