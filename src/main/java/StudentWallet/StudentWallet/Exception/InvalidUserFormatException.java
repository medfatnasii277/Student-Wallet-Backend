package StudentWallet.StudentWallet.Exception;

public class InvalidUserFormatException extends RuntimeException {
    public InvalidUserFormatException(String message) {
        super(message);
    }

    public InvalidUserFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
