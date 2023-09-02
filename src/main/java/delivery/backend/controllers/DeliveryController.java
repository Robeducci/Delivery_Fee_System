package delivery.backend.controllers;

import delivery.backend.entities.Delivery;
import delivery.backend.exceptions.ForbiddenUsageOfVehicleException;
import delivery.backend.exceptions.StationNotFoundException;
import delivery.backend.exceptions.WeatherDataNotFoundException;
import delivery.backend.exceptions.WrongDateException;
import delivery.backend.services.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Optional;


@RestController
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    /**
     * Endpoint for calculating the delivery fee based on station (City), vehicle type and (Optional) date.
     *
     * The Parameters are sent using GET.
     * With station and vehicle type a Delivery object is constructed with which the calculation of the fee is done
     * using DeliveryService method called "calculateDeliveryFee" that uses the latest weather data.
     *
     * If a date is given then the fee is calculated with weather data which has the closest import date to the
     * chosen date.
     *
     * @param wmoCode The wmo code of the weather station closest to the chosen city.
     * @param vehicle Type of the vehicle for the deliverer.
     * @param date Chosen date on which the fee calculations should be done.
     * @return An error message or the calculated delivery fee.
     */
    @GetMapping("/delivery")
    public String getDeliveryFee(@RequestParam(name = "station") String wmoCode,
                                 @RequestParam(name = "vehicle") String vehicle,
                                 @RequestParam(required = false, name = "date") String date) {
        try {
            Delivery delivery = new Delivery(wmoCode, vehicle);

            if (date == null || date.isEmpty()) {
                return deliveryService.calculateDeliveryFee(delivery, Optional.empty());
            } else if (LocalDateTime.now().isBefore(LocalDateTime.parse(date))) {
                throw new WrongDateException("Please do not select a future date");
            } else {
                return deliveryService.calculateDeliveryFee(delivery, Optional.of(date));
            }

        } catch (WeatherDataNotFoundException | StationNotFoundException
                | ForbiddenUsageOfVehicleException | WrongDateException e) {
            return e.getMessage();
        }
    }
}
