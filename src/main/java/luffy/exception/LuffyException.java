package luffy.exception;

/**
 * Custom exception class for the Luffy task management application. This exception is thrown when
 * there are application-specific errors such as invalid user input, command parsing failures, or
 * business logic violations. All error messages are prefixed with "OOPS!!!" to maintain the
 * application's character.
 */
public class LuffyException extends Exception {

    /**
     * Creates a new LuffyException with the specified error message.
     *
     * @param message the detailed error message describing what went wrong
     */
    public LuffyException(String message) {
        super(message);
    }

    /**
     * Returns the error message with "OOPS!!!" prefix. This maintains consistency with the
     * application's error message format.
     *
     * @return the formatted error message with "OOPS!!!" prefix
     */
    public String getMessage() {
        return "OOPS!!! " + super.getMessage();
    }
}
