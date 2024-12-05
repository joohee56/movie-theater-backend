package mt.movie_theater.api.exception;

public class CancelPaymentException extends RuntimeException {
    public CancelPaymentException() {

    }

    public CancelPaymentException(String message) {
        super(message);
    }

    public CancelPaymentException(String message, Throwable cause) {
        super(message, cause);
    }

    public CancelPaymentException(Throwable cause) {
        super(cause);
    }
}
