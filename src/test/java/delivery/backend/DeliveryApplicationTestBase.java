package delivery.backend;

import delivery.backend.entities.WeatherData;
import delivery.backend.repositories.WeatherRepository;
import delivery.backend.services.DeliveryService;
import delivery.backend.services.ImportWeatherTask;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@AutoConfigureMockMvc
@SpringBootTest
public class DeliveryApplicationTestBase {

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ImportWeatherTask importWeatherTask;
    @Autowired
    protected DeliveryService deliveryService;
    @Autowired
    protected WeatherRepository weatherRepository;

    protected static final String ERRORANSWER = "Encountered an Error. Please try again.";
    protected static final String FORBIDDENERROR = "Usage of selected vehicle type is forbidden";

    protected static final String TALLINN = "26038";
    protected static final String TARTU = "26242";
    protected static final String PARNU = "41803";
    protected static final String NARVA = "26058";

    protected static final String CAR = "car";
    protected static final String SCOOTER = "scooter";
    protected static final String BIKE = "bike";

    @BeforeEach
    void setUp() {
        weatherRepository.deleteAll();
    }

    protected void saveGoodWeatherDataToDatabase() {
        WeatherData tallinnData
                = new WeatherData("Tallinn-Harku", 26038,
                20.0, 2.0, "Overcast", BigInteger.valueOf( 1693131973L));

        WeatherData tartuData
                = new WeatherData("Tartu-Tõravere", 26242,
                20.0, 2.0, "Overcast", BigInteger.valueOf( 1693131973L));

        WeatherData parnuData
                = new WeatherData("Pärnu", 41803,
                20.0, 2.0, "Overcast", BigInteger.valueOf( 1693131973L));

        List<WeatherData> data = new ArrayList<>(Arrays.asList(tallinnData, tartuData, parnuData));

        weatherRepository.saveAll(data);
    }

    protected void saveBadWeatherDataToDatabase() {
        WeatherData tallinnData
                = new WeatherData("Tallinn-Harku", 26038,
                -2.0, 2.0, "Rain fall", BigInteger.valueOf( 1693131973L));

        WeatherData tartuData
                = new WeatherData("Tartu-Tõravere", 26242,
                20.0, 12.0, "Snow fall", BigInteger.valueOf( 1693131973L));

        WeatherData parnuData
                = new WeatherData("Pärnu", 41803,
                -20.0, 2.0, "Overcast", BigInteger.valueOf( 1693131973L));

        List<WeatherData> data = new ArrayList<>(Arrays.asList(tallinnData, tartuData, parnuData));

        weatherRepository.saveAll(data);
    }

    protected void saveWorseWeatherDataToDatabase() {
        WeatherData tallinnData
                = new WeatherData("Tallinn-Harku", 26038,
                -2.0, 2.0, "Thunder", BigInteger.valueOf( 1693131973L));
        WeatherData tartuData
                = new WeatherData("Tartu-Tõravere", 26242,
                20.0, 12.0, "Glaze and hail", BigInteger.valueOf( 1693131973L));
        WeatherData parnuData
                = new WeatherData("Pärnu", 41803,
                20.0, 22.0, "Snow fall", BigInteger.valueOf( 1693131973L));

        List<WeatherData> data = new ArrayList<>(Arrays.asList(tallinnData, tartuData, parnuData));

        weatherRepository.saveAll(data);
    }

}
