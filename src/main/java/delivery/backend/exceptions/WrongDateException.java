package delivery.backend.exceptions;

public class WrongDateException extends RuntimeException {

    /**
     * Exception that is thrown when the customer select a future date or a date we dont have data on.
     * @param error message.
     */
    public WrongDateException(String error) {
        super(error);
    }
}
