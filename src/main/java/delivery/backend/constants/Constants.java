package delivery.backend.constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Constants {

    public static final double BASEFEE4 = 4;
    public static final double BASEFEE35 = 3.5;
    public static final double BASEFEE3 = 3;
    public static final double BASEFEE25 = 2.5;
    public static final double BASEFEE2 = 2;

    public static final double EXTRAFEE1 = 1;
    public static final double EXTRAFEE05 = 0.5;

    public static final int MINWINDSPEED = 10;
    public static final int MAXWINDSPEED = 20;

    public static final int MINAIRTEMP = -10;

    public static final List<String> IMPORTANTDATAKEYS
         = new ArrayList<>(Arrays.asList("name", "wmocode", "airtemperature", "windspeed", "phenomenon"));

    public static final List<String> STATIONNAMES
         = new ArrayList<>(Arrays.asList("Tallinn-Harku", "Tartu-Tõravere", "Pärnu"));

    public static final String WEATHERURL = "https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php";

}
