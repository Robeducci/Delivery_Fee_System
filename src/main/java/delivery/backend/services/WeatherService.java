package delivery.backend.services;

import delivery.backend.entities.WeatherData;
import delivery.backend.exceptions.WeatherDataNotFoundException;
import delivery.backend.repositories.WeatherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Optional;

@Service
public class WeatherService {

    @Autowired
    private WeatherRepository weatherRepository;


    public WeatherData getLatestWeatherReportByStation(int wmoCode) {
        Optional<WeatherData> weatherReport = weatherRepository.findAllByWmoCode(wmoCode).stream()
                .max(Comparator.comparing(WeatherData::getTimestamp));

        return Optional.of(weatherReport).get()
                .orElseThrow(() -> {throw new WeatherDataNotFoundException("Did not find weather report!");});
    }
}
