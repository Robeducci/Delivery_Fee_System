package delivery.backend.services;

import delivery.backend.entities.WeatherData;
import delivery.backend.exceptions.WeatherDataNotFoundException;
import delivery.backend.exceptions.WrongDateException;
import delivery.backend.repositories.WeatherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WeatherService {

    private final WeatherRepository weatherRepository;

    private static final long DAYINSECONDS = 86400;


    /**
     * Getting the weather data from the weather station near the chosen city using station's wmo code.
     *
     * If a date is not chosen:
     *              First find all the weather reports from the chosen station, then
     *              compare import dates and find the latest (the biggest date).
     *
     * If a date is chosen:
     *              We find the closest weather data by comparing import dates, the chosen date and the time difference
     *              between given dates.
     *
     * @param wmoCode of the weather station.
     * @param date is the chosen date (Optional).
     * @return found weather data.
     * @throws WeatherDataNotFoundException when weather data is not found.
     * @throws WrongDateException when the date difference between chosen date and closest weather data import date is
     *                            longer than 1 day.
     */
    public WeatherData getWeatherData(int wmoCode, Optional<String> date) throws WeatherDataNotFoundException,
                                                                                    WrongDateException {

        List<WeatherData> weatherReports = weatherRepository.findAllByWmoCode(wmoCode);

        if (date.isEmpty()) {

            Optional<WeatherData> weatherData = weatherReports.stream()
                    .max(Comparator.comparing(WeatherData::getTimestamp));

            return Optional.of(weatherData).get()
                    .orElseThrow(() -> {throw new WeatherDataNotFoundException("Did not find weather data for station");});
        } else {

            LocalDateTime chosenDate = getLocalDateTimeFromString(date.get());
            return findClosestWeatherDataToDate(weatherReports, chosenDate);
        }
    }

    private WeatherData findClosestWeatherDataToDate(List<WeatherData> weatherReports,
                                                     LocalDateTime chosenDateTime) throws WrongDateException {

        WeatherData closestWeatherData = null;

        for (WeatherData weatherData : weatherReports) {
            if (weatherData.getTimestamp().isBefore(chosenDateTime)) {

                closestWeatherData = weatherData;

            } else if (weatherData.getTimestamp().isAfter(chosenDateTime)) {

                closestWeatherData = calculateClosestToChosenDate(closestWeatherData, weatherData, chosenDateTime);
                break;
            }
        }
        return closestWeatherData;
    }

    private WeatherData calculateClosestToChosenDate(WeatherData dataBeforeDate,
                                                     WeatherData dataAfterDate,
                                                     LocalDateTime chosenDateTime) throws WrongDateException {

        long dateBeforeSec;
        long dateAfterSec = getSecondsFromLocalDateTime(dataAfterDate.getTimestamp());
        long chosenDateSec = getSecondsFromLocalDateTime(chosenDateTime);

        long dateBeforeDiff;
        long dateAfterDiff = Math.abs(chosenDateSec - dateAfterSec);

        // If date before chosen date does not exist
        // The difference is the biggest it can be
        if (dataBeforeDate == null) {
            dateBeforeDiff = getSecondsFromLocalDateTime(chosenDateTime);
        } else {
            dateBeforeSec = getSecondsFromLocalDateTime(dataBeforeDate.getTimestamp());
            dateBeforeDiff = Math.abs(chosenDateSec - dateBeforeSec);
        }

        if (dateBeforeDiff >= dateAfterDiff && dateAfterDiff <= DAYINSECONDS) {
            return dataAfterDate;
        } else if (dateBeforeDiff <= DAYINSECONDS) {
            return dataBeforeDate;
        } else {
            throw new WrongDateException("We can't calculate fee for this date. " +
                    "Our closest weather data is more than 1 day apart");
        }
    }

    private long getSecondsFromLocalDateTime(LocalDateTime date) {
        ZonedDateTime ZonedDateTime = date.atZone(ZoneId.of("Europe/Tallinn"));
        return ZonedDateTime.toInstant().toEpochMilli() / 1000;
    }

    private LocalDateTime getLocalDateTimeFromString(String date) {
        LocalDateTime localDateTime = LocalDateTime.parse(date);
        ZonedDateTime ZonedDateTime = localDateTime.atZone(ZoneId.of("Europe/Tallinn"));
        return ZonedDateTime.toLocalDateTime();
    }
}
