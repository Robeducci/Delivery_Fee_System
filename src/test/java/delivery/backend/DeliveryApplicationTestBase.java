package delivery.backend;

import delivery.backend.entities.WeatherData;
import delivery.backend.repositories.WeatherRepository;
import delivery.backend.services.ImportWeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@AutoConfigureMockMvc
@SpringBootTest
public class DeliveryApplicationTestBase {

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ImportWeatherService importWeatherService;
    @Autowired
    protected WeatherRepository weatherRepository;

    protected static final String WEATHERDATANOTFOUNDERROR = "Did not find weather data for station";
    protected static final String DATESTOOFARAPARTERROR = "We can't calculate fee for this date." +
            " Our closest weather data is more than 1 day apart";
    protected static final String FUTUREDATEERROR = "Please do not select a future date";
    protected static final String STATIONNOTFOUNDERROR = "Weather station for this city was not found";
    protected static final String FORBIDDENERROR = "Usage of selected vehicle type is forbidden";

    protected static final String TALLINN = "26038";
    protected static final String TARTU = "26242";
    protected static final String PARNU = "41803";
    protected static final String NARVA = "26058";

    protected static final String CAR = "car";
    protected static final String SCOOTER = "scooter";
    protected static final String BIKE = "bike";

    protected static final LocalDateTime BASEDATE = LocalDateTime
            .ofInstant(Instant.ofEpochSecond(1693131973L), ZoneId.of("Europe/Tallinn"));

    @BeforeEach
    void setUp() {
        weatherRepository.deleteAll();
    }

    protected void saveGoodWeatherDataToDatabase() {
        WeatherData tallinnData
                = new WeatherData("Tallinn-Harku", 26038,
                20.0, 2.0, "Overcast", BASEDATE);

        WeatherData tartuData
                = new WeatherData("Tartu-Tõravere", 26242,
                20.0, 2.0, "Overcast", BASEDATE);

        WeatherData parnuData
                = new WeatherData("Pärnu", 41803,
                20.0, 2.0, "Overcast", BASEDATE);

        List<WeatherData> data = new ArrayList<>(Arrays.asList(tallinnData, tartuData, parnuData));

        weatherRepository.saveAll(data);
    }

    protected void saveBadWeatherDataToDatabase() {
        WeatherData tallinnData
                = new WeatherData("Tallinn-Harku", 26038,
                -2.0, 2.0, "Rain fall", BASEDATE);

        WeatherData tartuData
                = new WeatherData("Tartu-Tõravere", 26242,
                20.0, 12.0, "Snow fall", BASEDATE);

        WeatherData parnuData
                = new WeatherData("Pärnu", 41803,
                -20.0, 2.0, "Overcast", BASEDATE);

        List<WeatherData> data = new ArrayList<>(Arrays.asList(tallinnData, tartuData, parnuData));

        weatherRepository.saveAll(data);
    }

    protected void saveWorseWeatherDataToDatabase() {
        WeatherData tallinnData
                = new WeatherData("Tallinn-Harku", 26038,
                -2.0, 2.0, "Thunder", BASEDATE);
        WeatherData tartuData
                = new WeatherData("Tartu-Tõravere", 26242,
                20.0, 12.0, "Glaze and hail", BASEDATE);
        WeatherData parnuData
                = new WeatherData("Pärnu", 41803,
                20.0, 22.0, "Snow fall", BASEDATE);

        List<WeatherData> data = new ArrayList<>(Arrays.asList(tallinnData, tartuData, parnuData));

        weatherRepository.saveAll(data);
    }

    protected void saveDifferentDatesDifferentWeather() {

        WeatherData tallinnData1
                = new WeatherData("Tallinn-Harku", 26038,
                8.0, 2.0, "Rain fall",  LocalDateTime.of(2023, 6, 15, 15, 15));
        WeatherData tallinnData2
                = new WeatherData("Tallinn-Harku", 26038,
                -2.0, 2.0, "Snow",  LocalDateTime.of(2023, 6, 30, 15, 15));
        WeatherData tallinnData3
                = new WeatherData("Tallinn-Harku", 26038,
                10.0, 15.0, "Thunder",  LocalDateTime.of(2023, 7, 15, 11, 15));
        WeatherData tallinnData4
                = new WeatherData("Tallinn-Harku", 26038,
                10.0, 15.0, "Overcast",  LocalDateTime.of(2023, 7, 15, 12, 15));
        WeatherData tallinnData5
                = new WeatherData("Tallinn-Harku", 26038,
                -2.0, 2.0, "Overcast",  LocalDateTime.of(2023, 7, 30, 15, 15));
        WeatherData tallinnData6
                = new WeatherData("Tallinn-Harku", 26038,
                -15.0, 2.0, "Overcast",  LocalDateTime.of(2023, 8, 15, 15, 15));

        List<WeatherData> data = new ArrayList<>(Arrays.asList(tallinnData1, tallinnData2, tallinnData3,
                tallinnData4, tallinnData5, tallinnData6));

        weatherRepository.saveAll(data);
    }
}
