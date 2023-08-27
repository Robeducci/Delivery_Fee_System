package delivery.backend.entities;

import delivery.backend.enums.Station;
import delivery.backend.enums.Vehicle;
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
     * Has two fields: Enum Station and Enum Vehicle
     *
     * Enums are used so no random stations or vehicles can be got,
     * and it is easy to add new stations or vehicle types when needed
     *
     * @param wmoCode The wmo code of the nearest station to the chosen city.
     * @param vehicle Type of the vehicle the customer has chosen for the deliverer.
     */
    public Delivery(String wmoCode, String vehicle)  {
        this.station = Station.fromWmoCode(Integer.parseInt(wmoCode));
        this.vehicle = Vehicle.valueOf(vehicle.toUpperCase());
    }
}
