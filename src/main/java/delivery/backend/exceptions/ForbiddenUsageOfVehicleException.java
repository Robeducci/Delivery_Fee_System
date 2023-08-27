package delivery.backend.exceptions;

public class ForbiddenUsageOfVehicleException extends RuntimeException {

    /**
     * Exception that is thrown when the weather conditions are too dangerous for certain vehicle types.
     * @param error message.
     */
    public ForbiddenUsageOfVehicleException(String error) {
        super(error);
    }
}
