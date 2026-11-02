package atm.exception;

/** Raised when the local educational-simulator data files cannot be used. */
public class StorageException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public StorageException(String message, Throwable cause) { super(message, cause); }
}
