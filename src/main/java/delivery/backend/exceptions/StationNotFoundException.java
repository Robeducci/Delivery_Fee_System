package delivery.backend.exceptions;

public class StationNotFoundException extends RuntimeException {

    /**
     * Exception that is thrown when a station with a certain wmo code is not found.
     * @param error message.
     */
    public StationNotFoundException(String error) {
        super(error);
    }
}
