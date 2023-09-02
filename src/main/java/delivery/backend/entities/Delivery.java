package delivery.backend.entities;

import delivery.backend.enums.Station;
import delivery.backend.enums.Vehicle;
import delivery.backend.exceptions.StationNotFoundException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Delivery {

    private final Station station;

    private final Vehicle vehicle;

    /**
     * Delivery object.
     *
     * Has two fields: Station and Vehicle
     * Enums are used so no random stations or vehicles can be used.
     *
     * @param wmoCode The wmo code of the nearest station to the chosen city.
     * @param vehicle Type of the vehicle for the deliverer.
     * @throws StationNotFoundException when a station with the given wmoCode is not found.
     */
    public Delivery(String wmoCode, String vehicle) throws StationNotFoundException {
        this.station = Station.fromWmoCode(Integer.parseInt(wmoCode));
        this.vehicle = Vehicle.valueOf(vehicle.toUpperCase());
    }
}
