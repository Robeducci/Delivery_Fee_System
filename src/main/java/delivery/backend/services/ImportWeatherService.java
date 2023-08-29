package delivery.backend.services;

import delivery.backend.entities.WeatherData;
import delivery.backend.repositories.WeatherRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class ImportWeatherService {

    private final WeatherRepository weatherRepository;

    private final List<String> importantDataNames
            = new ArrayList<>(Arrays.asList("name", "wmocode", "airtemperature", "windspeed", "phenomenon"));

    private final List<String> importantStationNames
            = new ArrayList<>(Arrays.asList("Tallinn-Harku", "Tartu-Tõravere", "Pärnu"));

    private final String weatherUrl = "https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php";

    private final Logger logger = Logger.getLogger(ImportWeatherService.class.getName());


    /**
     * Importing wheather data for certain stations from "www.ilmateenistus.ee".
     *
     * This task runs first when the application has started and then every hour at 15 minutes.
     *
     * A URLConnection is made. The data is in the form of a xml, so it can be handled as a xml.
     * A Document is created from the URLConnection's inputStream.
     * From the Document we take:
     *           - TimeStamp - Time at which the weather recordings were taken.
     *           - Stations  - Only stations that are near cities we deliver in.
     *
     * Then only the data needed for calculations are saved into the database as WeatherData object.
     */
    @PostConstruct
    @Scheduled(cron = "0 15 * * * *")
    public void importWeatherData() {
        try {
            URLConnection urlConnection = new URL(weatherUrl).openConnection();
            urlConnection.addRequestProperty("Accept", "application/xml");


            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(urlConnection.getInputStream());

            NodeList stationNodes = doc.getDocumentElement().getChildNodes();
            BigInteger timestamp = new BigInteger(doc.getDocumentElement().getAttribute("timestamp"));

            List<Node> stations = IntStream.range(0, stationNodes.getLength())
                    .mapToObj(stationNodes::item)
                    .filter(Node::hasChildNodes)
                    .filter(s -> importantStationNames.contains(s.getChildNodes().item(1).getTextContent()))
                    .collect(Collectors.toList());

            saveWeatherData(stations, timestamp);

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            logger.log(Level.INFO, "WEATHER DATA: Imported weather data at: " + dtf.format(now));

        } catch (IOException | ParserConfigurationException | SAXException e) {
            logger.log(Level.WARNING, "Weather info was not imported. Encountered an error", e);
        }
    }

    private void saveWeatherData(List<Node> stations, BigInteger timeStamp) {

        for (Node station : stations) {
            List<Node> stationData = IntStream.range(0, station.getChildNodes().getLength())
                    .mapToObj(station.getChildNodes()::item)
                    .filter(node -> !Objects.equals(node.getNodeName(), "#text")
                            && importantDataNames.contains(node.getNodeName()))
                    .collect(Collectors.toList());

            WeatherData weatherData = createWeatherDataFromImport(stationData, timeStamp);
            weatherRepository.save(weatherData);
        }
    }

    private WeatherData createWeatherDataFromImport(List<Node> stationData, BigInteger timeStamp) {

        WeatherData weatherData = new WeatherData();
        weatherData.setTimestamp(LocalDateTime
                .ofInstant(Instant.ofEpochSecond(timeStamp.longValue()), ZoneId.of("Europe/Tallinn")));

        for (Node attribute : stationData) {
            switch (attribute.getNodeName()) {
                case "name" -> weatherData.setStationName(stationData.get(0).getTextContent());
                case "wmocode" -> weatherData.setWmoCode(Integer.parseInt(stationData.get(1).getTextContent()));
                case "phenomenon" -> weatherData.setPhenomenon(stationData.get(2).getTextContent());
                case "airtemperature" -> weatherData.setAirTemp(Float.parseFloat(stationData.get(3).getTextContent()));
                case "windspeed" -> weatherData.setWindSpeed(Float.parseFloat(stationData.get(4).getTextContent()));
            }
        }
        return weatherData;
    }
}
