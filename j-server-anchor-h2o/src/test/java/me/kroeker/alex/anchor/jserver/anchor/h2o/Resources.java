package me.kroeker.alex.anchor.jserver.anchor.h2o;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ak902764
 */
public class Resources {
    static final String TITANIC_DATA = "csv/titanic-data.csv";

    static final String TITANIC_CLASSIFIER = "mojo/titanic-DRF_model_R_1543247649385_1.zip";

    static final String AIRLINE_CLASSIFIER = "mojo/GBM_Airlines_Classification.zip";

    static final List<String> AIRLINE_FEATURES = Arrays.asList("Year", "Month", "DayofMonth", "DayOfWeek",
            "DepTime", "CRSDepTime", "ArrTime", "CRSArrTime", "UniqueCarrier", "FlightNum", "TailNum",
            "ActualElapsedTime", "CRSElapsedTime", "AirTime", "ArrDelay", "DepDelay", "Origin", "Dest", "Distance",
            "TaxiIn", "TaxiOut", "Cancelled", "CancellationCode", "Diverted", "CarrierDelay", "WeatherDelay",
            "NASDelay", "SecurityDelay", "LateAircraftDelay", "IsArrDelayed", "IsDepDelayed");

    static final Map<String, Integer> AIRLINE_FEATURE_MAPPING = new HashMap<>(AIRLINE_FEATURES.size());

    static {
        for (int i = 0; i < AIRLINE_FEATURES.size(); i++) {
            AIRLINE_FEATURE_MAPPING.put(AIRLINE_FEATURES.get(i), i);
        }
    }
}
