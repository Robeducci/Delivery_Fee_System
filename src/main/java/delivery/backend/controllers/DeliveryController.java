package delivery.backend.controllers;

import delivery.backend.entities.Delivery;
import delivery.backend.exceptions.StationNotFoundException;
import delivery.backend.exceptions.WeatherDataNotFoundException;
import delivery.backend.services.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class DeliveryController {

    @Autowired
    private DeliveryService deliveryService;

    /**
     * Endpoint for calculating the delivery fee based on station (City) and vehicle.
     *
     * The Parameters are sent using POST.
     * With these parameters we construct a Delivery object with which we can start calculating the fee
     * using DeliveryService method called "calculateDeliveryFee"
     *
     * If we are given an unknown station, the "StationNotFoundException" will be thrown and the customer
     * will be asked to try again.
     *
     * If we lack the weather data needed for the calculations, the "WeatherDataNotFoundException" will be
     * thrown and the customer will be asked to try again.
     *
     * @param wmoCode The wmo code of the weather station closest to the chosen city.
     * @param vehicle Type of the vehicle the customer has chosen for the deliverer.
     * @return An error message or the calculated delivery fee.
     */
    @PostMapping("/delivery")
    public String getDeliveryFee(@RequestParam(name = "station") String wmoCode,
                                      @RequestParam(name = "vehicle") String vehicle) {
        try {
            Delivery delivery = new Delivery(wmoCode, vehicle);

            return deliveryService.calculateDeliveryFee(delivery);
        } catch (WeatherDataNotFoundException | StationNotFoundException e) {
            return "Encountered an Error. Please try again.";
        }
    }
}
