package delivery.backend.services;

import delivery.backend.DeliveryApplicationTestBase;
import delivery.backend.entities.WeatherData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

class ImportWeatherTaskTest extends DeliveryApplicationTestBase {

    @Test
    void testImportWeatherData() {
        Assertions.assertEquals(0, weatherRepository.findAll().size());

        importWeatherTask.importWeatherData();

        Assertions.assertEquals(3, weatherRepository.findAll().size());

        WeatherData weatherData = weatherRepository.findAllByWmoCode(26038).get(0);

        Assertions.assertNotNull(weatherData.getId());
        Assertions.assertNotNull(weatherData.getStationName());
        Assertions.assertNotNull(weatherData.getWmoCode());
        Assertions.assertNotNull(weatherData.getPhenomenon());
        Assertions.assertNotNull(weatherData.getTimestamp());

    }
}