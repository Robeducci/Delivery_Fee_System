package delivery.backend.exceptions;

public class WeatherDataNotFoundException extends RuntimeException {

    /**
     * Exception that is thrown when weather data for the chosen station is not found/not saved in our database.
     * @param error message.
     */
    public WeatherDataNotFoundException(String error) {
        super(error);
    }
}
