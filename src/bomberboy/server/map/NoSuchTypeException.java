package bomberboy.server.map;

public class NoSuchTypeException extends Exception {
    private char type;

    public NoSuchTypeException(char type) {
        this.type = type;
    }

    public String getMessage() {
        return "The type " + type + " does not exist!";
    }
}
