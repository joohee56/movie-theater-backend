package mt.movie_theater.api.exception;

public class DuplicateSeatBookingException extends RuntimeException {
    public DuplicateSeatBookingException() {

    }

    public DuplicateSeatBookingException(String message) {
        super(message);
    }

    public DuplicateSeatBookingException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateSeatBookingException(Throwable cause) {
        super(cause);
    }
}
