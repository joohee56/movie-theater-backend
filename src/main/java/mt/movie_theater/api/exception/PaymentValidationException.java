package mt.movie_theater.api.exception;

public class PaymentValidationException extends RuntimeException {
    public PaymentValidationException() {

    }

    public PaymentValidationException(String message) {
        super(message);
    }

    public PaymentValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public PaymentValidationException(Throwable cause) {
        super(cause);
    }
}
