package delivery.backend.exceptions;

public class StationNotFoundException extends RuntimeException {

    /**
     * Exception that is thrown when we do not have the chosen station (city) saved in our database.
     * @param error message.
     */
    public StationNotFoundException(String error) {
        super(error);
    }
}
