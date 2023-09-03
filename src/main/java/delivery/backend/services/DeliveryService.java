package delivery.backend.services;

import delivery.backend.constants.Constants;
import delivery.backend.entities.Delivery;
import delivery.backend.entities.WeatherData;
import delivery.backend.enums.Station;
import delivery.backend.exceptions.ForbiddenUsageOfVehicleException;
import delivery.backend.exceptions.WrongDateException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final WeatherService weatherService;

    /**
     * Method for calculating delivery fee based on city (station wmo code), vehicle type and weather conditions.
     *
     * First we get the latest weather data from the station closest to the chosen city.
     *
     * We need to calculate: (Extra fees apply to certain vehicle types)
     *      - rbf (regional base fee)             - base fee for certain vehicle types in certain cities.
     *      - atef (air temperature extra fee)    - (Bike and Scooter) based on how cold it is, extra fee is added.
     *      - wsef (wind speed extra fee)         - (Bike) if it is windy then an extra fee is added. If it is too windy
     *                                              then it is too dangerous to deliver using the chosen vehicle type.
     *      - wpef (weather phenomenon extra fee) - (Bike and Scooter) Snow, sleet and rain give extra fees.
     *                                              Glaze, hail and thunder are too dangerous to make deliveries
     *                                              in using these certain vehicle types.
     *
     * @param delivery Object with the chosen station (city) and vehicle type for this delivery.
     * @param date Optional parameter. If a date is chosen then the fee will be calculated based on the chosen date.
     * @return Error message or the calculated delivery fee.
     * @throws ForbiddenUsageOfVehicleException Thrown when weather conditions are too dangerous for vehicle type.
     * @throws WrongDateException Thrown when the chosen date is too far apart with the closest weather data.
     */
    public String calculateDeliveryFee(Delivery delivery, Optional<String> date)
            throws ForbiddenUsageOfVehicleException, WrongDateException {

        WeatherData weatherData = weatherService.getWeatherData(delivery.getStation().getWmoCode(), date);

        double fee = getRegionalBaseFee(delivery);
        double atef = 0;
        double wpef = 0;
        double wsef = 0;

        switch (delivery.getVehicle()) {
            case SCOOTER -> {
                atef = calculateAirTemperatureFee(weatherData.getAirTemp());
                wpef = calculateWeatherPhenomenon(weatherData.getPhenomenon().toLowerCase());
            }
            case BIKE -> {
                atef = calculateAirTemperatureFee(weatherData.getAirTemp());
                wpef = calculateWeatherPhenomenon(weatherData.getPhenomenon().toLowerCase());
                wsef = calculateWindSpeedFee(weatherData.getWindSpeed());
            }
        }

        return fee + atef + wpef + wsef + " €";
    }

    private double getRegionalBaseFee(Delivery delivery) {

        if (delivery.getStation() == Station.TALLINN) {
            return switch (delivery.getVehicle()) {
                case CAR -> Constants.BASEFEE4;
                case SCOOTER -> Constants.BASEFEE35;
                case BIKE -> Constants.BASEFEE3;
            };
        } else if (delivery.getStation() == Station.TARTU) {
            return switch (delivery.getVehicle()) {
                case CAR -> Constants.BASEFEE35;
                case SCOOTER -> Constants.BASEFEE3;
                case BIKE -> Constants.BASEFEE25;
            };
        } else {
            return switch (delivery.getVehicle()) {
                case CAR -> Constants.BASEFEE3;
                case SCOOTER -> Constants.BASEFEE25;
                case BIKE -> Constants.BASEFEE2;
            };
        }
    }

    private double calculateAirTemperatureFee(double airTemp) {

        if (airTemp < Constants.MINAIRTEMP) {
            return Constants.EXTRAFEE1;
        } else if (Constants.MINAIRTEMP <= airTemp && airTemp <= 0) {
            return Constants.EXTRAFEE05;
        } else {
            return 0;
        }
    }

    private double calculateWindSpeedFee(double windSpeed) throws ForbiddenUsageOfVehicleException {

        if (Constants.MINWINDSPEED <= windSpeed && windSpeed <= Constants.MAXWINDSPEED) {
            return Constants.EXTRAFEE05;
        } else if (windSpeed > Constants.MAXWINDSPEED) {
            throw new ForbiddenUsageOfVehicleException("Usage of selected vehicle type is forbidden");
        } else {
            return 0;
        }
    }

    private double calculateWeatherPhenomenon(String phenomenon) throws ForbiddenUsageOfVehicleException {

        if (phenomenon.contains("rain")) {
            return Constants.EXTRAFEE05;
        } else if (phenomenon.contains("snow") || phenomenon.contains("sleet")) {
            return Constants.EXTRAFEE1;
        } else if (phenomenon.contains("glaze") || phenomenon.contains("hail") || phenomenon.contains("thunder")) {
            throw new ForbiddenUsageOfVehicleException("Usage of selected vehicle type is forbidden");
        } else {
            return 0;
        }
    }
}
