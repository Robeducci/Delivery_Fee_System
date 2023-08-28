package delivery.backend.entities;



import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class WeatherData {

    @Id
    @GeneratedValue
    private Long id;

    private String stationName;

    private Integer wmoCode;

    private double airTemp;

    private double windSpeed;

    private String phenomenon;

    private LocalDateTime timestamp;


    /**
     * WeatherData object.
     *
     * Entity class to be saved in our database.
     *
     * Fields are used in the calculation of the delivery fee.
     *
     * @param stationName The name of the weather station.
     * @param wmoCode The wmo code of the weather station.
     * @param airTemp Air temperature recorded at the weather station.
     * @param windSpeed Wind speed recorded at the weather station.
     * @param phenomenon Weather phenomenon recorded at the weather station.
     * @param timestamp Time at which these recordings were taken at the weather station.
     */
    public WeatherData(String stationName, Integer wmoCode, double airTemp,
                       double windSpeed, String phenomenon, LocalDateTime timestamp) {
        this.stationName = stationName;
        this.wmoCode = wmoCode;
        this.airTemp = airTemp;
        this.windSpeed = windSpeed;
        this.phenomenon = phenomenon;
        this.timestamp = timestamp;
    }
}
