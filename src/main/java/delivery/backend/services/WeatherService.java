package delivery.backend.services;

        import delivery.backend.entities.WeatherData;
        import delivery.backend.exceptions.WeatherDataNotFoundException;
        import delivery.backend.repositories.WeatherRepository;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.stereotype.Service;

        import java.math.BigInteger;
        import java.time.Instant;
        import java.time.LocalDateTime;
        import java.time.ZoneId;
        import java.time.ZonedDateTime;
        import java.util.Comparator;
        import java.util.Iterator;
        import java.util.List;
        import java.util.Optional;
        import java.util.stream.Collectors;

@Service
public class WeatherService {

    @Autowired
    private WeatherRepository weatherRepository;

    private static final long TWODAYSINSECONDS = 172800;


    public WeatherData getLatestWeatherReportByStation(int wmoCode) {
        Optional<WeatherData> weatherReport = weatherRepository.findAllByWmoCode(wmoCode).stream()
                .max(Comparator.comparing(WeatherData::getTimestamp));

        return Optional.of(weatherReport).get()
                .orElseThrow(() -> {throw new WeatherDataNotFoundException("Did not find weather report!");});
    }

    public WeatherData getClosestWeatherReportToDateByStation(int wmoCode, String dateSeconds) {
        List<WeatherData> weatherReports = weatherRepository.findAllByWmoCode(wmoCode);
        LocalDateTime chosenDate = getLocalDateTimeFromString(dateSeconds);

        return findClosestWeatherDataToDate(weatherReports, chosenDate);
    }

    private LocalDateTime getLocalDateTimeFromString(String date) {
        LocalDateTime localDateTime = LocalDateTime.parse(date);
        ZonedDateTime ZonedDateTime = localDateTime.atZone(ZoneId.of("Europe/Tallinn"));
        return ZonedDateTime.toLocalDateTime();
    }

    private WeatherData findClosestWeatherDataToDate(List<WeatherData> weatherReports, LocalDateTime chosenDateTime) {


        WeatherData closestWeatherDataAtm = null;

        for (WeatherData weatherData : weatherReports) {

            if (weatherData.getTimestamp().isBefore(chosenDateTime)) {
                closestWeatherDataAtm = weatherData;

            } else if (weatherData.getTimestamp().isAfter(chosenDateTime)) {
                if (closestWeatherDataAtm != null) {

                    closestWeatherDataAtm = calculateClosestToChosenDate(closestWeatherDataAtm, weatherData, chosenDateTime);

                } else if (!differenceIsBiggerThanTwoDays(chosenDateTime, weatherData.getTimestamp())) {
                    closestWeatherDataAtm = weatherData;
                }
            }
        }
        return closestWeatherDataAtm;
    }

    private boolean differenceIsBiggerThanTwoDays(LocalDateTime chosenDate, LocalDateTime dateAfter) {

        long dateAfterSeconds = getSecondsFromLocalDateTime(dateAfter);
        long chosenDateSeconds = getSecondsFromLocalDateTime(chosenDate);

        return (dateAfterSeconds - chosenDateSeconds) > TWODAYSINSECONDS;

    }

    private WeatherData calculateClosestToChosenDate(WeatherData dataBeforeDate,
                                                     WeatherData dataAfterDate,
                                                     LocalDateTime chosenDateTime) {

        long beforeDateSeconds = getSecondsFromLocalDateTime(dataBeforeDate.getTimestamp());
        long afterDateSeconds = getSecondsFromLocalDateTime(dataAfterDate.getTimestamp());
        long chosenDateSeconds = getSecondsFromLocalDateTime(chosenDateTime);

        if ((chosenDateSeconds - beforeDateSeconds) >= (afterDateSeconds - chosenDateSeconds)) {
            return dataAfterDate;
        } else {
            return dataBeforeDate;
        }
    }

    private long getSecondsFromLocalDateTime(LocalDateTime date) {
        ZonedDateTime ZonedDateTime = date.atZone(ZoneId.of("Europe/Tallinn"));
        return ZonedDateTime.toInstant().toEpochMilli() / 1000;
    }
}
