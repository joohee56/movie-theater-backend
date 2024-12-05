package mt.movie_theater.api.exception;

public class PreparePaymentException extends RuntimeException {
    public PreparePaymentException() {}

    public PreparePaymentException(String message) {
        super(message);
    }

    public PreparePaymentException(String message, Throwable cause) {
        super(message, cause);
    }

    public PreparePaymentException(Throwable cause) {
        super(cause);
    }
}
