package atm.exception;

/** Raised for a missing, duplicate, or invalid account. */
public class InvalidAccountException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public InvalidAccountException(String message) { super(message); }
}
