package me.kroeker.alex.anchor.jserver.anchor.h2o;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ak902764
 */
public class Resources {

    static final String AIRLINE_CSV = "csv/airline-data.csv";


    static final String AIRLINE_CLASSIFIER = "mojo/GBM_Airlines_Classification.zip";

    static final List<String> AIRLINE_FEATURES = Arrays.asList("Year", "Month", "DayofMonth", "DayOfWeek",
            "DepTime", "CRSDepTime", "ArrTime", "CRSArrTime", "UniqueCarrier", "FlightNum", "TailNum",
            "ActualElapsedTime", "CRSElapsedTime", "AirTime", "ArrDelay", "DepDelay", "Origin", "Dest", "Distance",
            "TaxiIn", "TaxiOut", "Cancelled", "CancellationCode", "Diverted", "CarrierDelay", "WeatherDelay",
            "NASDelay", "SecurityDelay", "LateAircraftDelay", "IsArrDelayed", "IsDepDelayed");

    static final Map<String, Integer> AIRLINE_FEATURE_MAPPING = new HashMap<>(AIRLINE_FEATURES.size());

    static final Object[] SIMPLE_AIRLINE_INSTANCE;

    static {
        for (int i = 0; i < AIRLINE_FEATURES.size(); i++) {
            AIRLINE_FEATURE_MAPPING.put(AIRLINE_FEATURES.get(i), i);
        }

        SIMPLE_AIRLINE_INSTANCE = new Object[31];
        SIMPLE_AIRLINE_INSTANCE[0] = 1987;
        SIMPLE_AIRLINE_INSTANCE[1] = 10;
        SIMPLE_AIRLINE_INSTANCE[2] = 14;
        SIMPLE_AIRLINE_INSTANCE[3] = 3;
        SIMPLE_AIRLINE_INSTANCE[4] = 741;
        SIMPLE_AIRLINE_INSTANCE[5] = 730;
        SIMPLE_AIRLINE_INSTANCE[6] = 912;
        SIMPLE_AIRLINE_INSTANCE[7] = 849;
        SIMPLE_AIRLINE_INSTANCE[8] = "PS";
        SIMPLE_AIRLINE_INSTANCE[9] = 1451;
        SIMPLE_AIRLINE_INSTANCE[10] = "NA";
        SIMPLE_AIRLINE_INSTANCE[11] = 91;
        SIMPLE_AIRLINE_INSTANCE[12] = 79;
        SIMPLE_AIRLINE_INSTANCE[13] = null;
        SIMPLE_AIRLINE_INSTANCE[14] = 23;
        SIMPLE_AIRLINE_INSTANCE[15] = 11;
        SIMPLE_AIRLINE_INSTANCE[16] = "SAN";
        SIMPLE_AIRLINE_INSTANCE[17] = "SFO";
        SIMPLE_AIRLINE_INSTANCE[18] = 447;
        SIMPLE_AIRLINE_INSTANCE[19] = null;
        SIMPLE_AIRLINE_INSTANCE[20] = null;
        SIMPLE_AIRLINE_INSTANCE[21] = 0;
        SIMPLE_AIRLINE_INSTANCE[22] = "NA";
        SIMPLE_AIRLINE_INSTANCE[23] = 0;
        SIMPLE_AIRLINE_INSTANCE[24] = null;
        SIMPLE_AIRLINE_INSTANCE[25] = null;
        SIMPLE_AIRLINE_INSTANCE[26] = null;
        SIMPLE_AIRLINE_INSTANCE[27] = null;
        SIMPLE_AIRLINE_INSTANCE[28] = null;
        SIMPLE_AIRLINE_INSTANCE[29] = "YES";
        SIMPLE_AIRLINE_INSTANCE[30] = "YES";
    }
}
