package delivery.backend.services;

import delivery.backend.entities.Delivery;
import delivery.backend.entities.WeatherData;
import delivery.backend.enums.Station;
import delivery.backend.enums.Vehicle;
import delivery.backend.exceptions.ForbiddenUsageOfVehicleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeliveryService {

    @Autowired
    private WeatherService weatherService;

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
    public String calculateDeliveryFee(Delivery delivery) {
        WeatherData weatherData =
                weatherService.getLatestWeatherReportByStation(delivery.getStation().getWmoCode());

        double rbf = getRegionalBaseFee(delivery);
        double atef;
        double wsef = 0;
        double wpef;

        if (delivery.getVehicle() == Vehicle.CAR) {
            return rbf + " €";
        }
        if (delivery.getVehicle() == Vehicle.BIKE) {
            try {
                wsef = calculateWindSpeedFee(weatherData.getWindSpeed());
            } catch (ForbiddenUsageOfVehicleException e) {
                return e.getMessage();
            }
        }
        atef = calculateAirTemperatureFee(weatherData.getAirTemp());

        try {
            wpef = calculateWeatherPhenomenon(weatherData.getPhenomenon().toLowerCase());
        } catch (ForbiddenUsageOfVehicleException e) {
            return e.getMessage();
        }

        double fee = rbf + atef + wsef + wpef;


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
        double atef = 0;
        if (airTemp < -10) {
            atef = EXTRAFEE1;
        } else if (-10 <= airTemp && 0 >= airTemp) {
            atef = EXTRAFEE05;
        }
        return atef;
    }

    private double calculateWindSpeedFee(double windSpeed) {
        double wsef = 0;
        if (10 <= windSpeed && 20 >= windSpeed) {
            wsef = EXTRAFEE05;
        } else if (windSpeed > 20) {
            throw new ForbiddenUsageOfVehicleException("Usage of selected vehicle type is forbidden");
        }
        return wsef;
    }

    private double calculateWeatherPhenomenon(String phenomenon) {
        double wpef = 0;
        if (phenomenon.contains("rain")) {
            wpef = EXTRAFEE05;
        } else if (phenomenon.contains("snow") || phenomenon.contains("sleet")) {
            wpef = EXTRAFEE1;
        } else if (phenomenon.contains("glaze") || phenomenon.contains("hail") || phenomenon.contains("thunder")) {
            throw new ForbiddenUsageOfVehicleException("Usage of selected vehicle type is forbidden");
        }
        return wpef;
    }
}
