package delivery.backend.services;

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

    private static final double BASEFEE4 = 4;
    private static final double BASEFEE35 = 3.5;
    private static final double BASEFEE3 = 3;
    private static final double BASEFEE25 = 2.5;
    private static final double BASEFEE2 = 2;

    private static final double EXTRAFEE1 = 1;
    private static final double EXTRAFEE05 = 0.5;



    /**
     * Method for calculating delivery fee based on city (station wmo code), vehicle type and weather conditions.
     *
     * First we get the latest weather data from the station closest to the chosen city.
     *
     * We need to calculate: (Extra fees apply to certain vehicle types)
     *      - rbf (regional base fee)             - base fee for certain vehicle types in certain cities.
     *      - atef (air temperature extra fee)    - (Bike and Scooter) based on how cold it is, we add extra fee.
     *      - wsef (wind speed extra fee)         - (Bike) if it is windy then an extra fee is added. If it is too
     *                                              windy then it is too dangerous to deliver using this vehicle type.
     *      - wpef (weather phenomenon extra fee) - (Bike and Scooter) Snow, sleet and rain give extra fees.
     *                                              Glaze, hail and thunder are too dangerous to make deliveries
     *                                              in using these certain vehicle types.
     *
     * @param delivery object with the chosen station (city) and vehicle type for this delivery.
     * @return Error message or the calculated delivery fee.
     */
    public String calculateDeliveryFee(Delivery delivery, Optional<String> date)
            throws ForbiddenUsageOfVehicleException, WrongDateException {

        WeatherData weatherData = weatherService.getWeatherData(delivery.getStation().getWmoCode(), date);

        double fee = getRegionalBaseFee(delivery);
        double atef = calculateAirTemperatureFee(weatherData.getAirTemp());
        double wpef = calculateWeatherPhenomenon(weatherData.getPhenomenon().toLowerCase());
        double wsef = calculateWindSpeedFee(weatherData.getWindSpeed());

        switch (delivery.getVehicle()) {
            case SCOOTER -> fee += atef + wpef;
            case BIKE -> fee += atef + wsef + wpef;
        }

        return fee + " €";
    }

    private double getRegionalBaseFee(Delivery delivery) {

        if (delivery.getStation() == Station.TALLINN) {
            return switch (delivery.getVehicle()) {
                case CAR -> BASEFEE4;
                case SCOOTER -> BASEFEE35;
                case BIKE -> BASEFEE3;
            };
        } else if (delivery.getStation() == Station.TARTU) {
            return switch (delivery.getVehicle()) {
                case CAR -> BASEFEE35;
                case SCOOTER -> BASEFEE3;
                case BIKE -> BASEFEE25;
            };
        } else {
            return switch (delivery.getVehicle()) {
                case CAR -> BASEFEE3;
                case SCOOTER -> BASEFEE25;
                case BIKE -> BASEFEE2;
            };
        }
    }

    private double calculateAirTemperatureFee(double airTemp) {

        if (airTemp < -10) {
            return EXTRAFEE1;
        } else if (-10 <= airTemp && airTemp <= 0) {
            return EXTRAFEE05;
        } else {
            return 0;
        }
    }

    private double calculateWindSpeedFee(double windSpeed) throws ForbiddenUsageOfVehicleException {

        if (10 <= windSpeed && windSpeed <= 20) {
            return EXTRAFEE05;
        } else if (windSpeed > 20) {
            throw new ForbiddenUsageOfVehicleException("Usage of selected vehicle type is forbidden");
        } else {
            return 0;
        }
    }

    private double calculateWeatherPhenomenon(String phenomenon) throws ForbiddenUsageOfVehicleException {

        if (phenomenon.contains("rain")) {
            return EXTRAFEE05;
        } else if (phenomenon.contains("snow") || phenomenon.contains("sleet")) {
            return EXTRAFEE1;
        } else if (phenomenon.contains("glaze") || phenomenon.contains("hail") || phenomenon.contains("thunder")) {
            throw new ForbiddenUsageOfVehicleException("Usage of selected vehicle type is forbidden");
        } else {
            return 0;
        }
    }
}
