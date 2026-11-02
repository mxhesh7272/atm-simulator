package atm.exception;

/** Raised when an account cannot cover a withdrawal or transfer. */
public class InsufficientFundsException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public InsufficientFundsException(String message) { super(message); }
}
