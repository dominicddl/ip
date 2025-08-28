package luffy.exception;

public class LuffyException extends Exception {
    public LuffyException(String message) {
        super(message);
    }

    public String getMessage() {
        return "OOPS!!! " + super.getMessage();
    }
}
