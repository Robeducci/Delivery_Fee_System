package delivery.backend.exceptions;

public class WrongDateException extends RuntimeException {

    /**
     * Exception that is thrown when the customer has selected a future date or a date on which there is no data.
     * @param error message.
     */
    public WrongDateException(String error) {
        super(error);
    }
}
