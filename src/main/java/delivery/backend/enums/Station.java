package delivery.backend.enums;

import delivery.backend.exceptions.StationNotFoundException;

import java.util.Arrays;
import java.util.Optional;

/**
 * Stations.
 *
 * The different stations near cities we are doing deliveries in.
 *
 * Consists of CITY(wmo code of station)
 */
public enum Station {

    TALLINN(26038),
    TARTU(26242),
    PARNU(41803);

    private final int wmoCode;

    Station(int wmoCode) {
        this.wmoCode = wmoCode;
    }

    /**
     * Getting the wmo code of the weather station.
     *
     * @return The wmo code of the weather station.
     */
    public int getWmoCode() {
        return wmoCode;
    }

    /**
     * Getting the City from the weather station's wmo code.
     *
     * @param wmoCode The wmo code of the weather station.
     * @return The city near the weather station
     */
    public static Station fromWmoCode(int wmoCode) throws StationNotFoundException {

        Optional<Station> expectedStation = Arrays.stream(Station.values())
                .filter(station -> station.getWmoCode() == wmoCode)
                .findFirst();

        return Optional.of(expectedStation).get()
                .orElseThrow(()-> {throw new StationNotFoundException("Weather station for this city was not found");});
    }
}
