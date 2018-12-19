package de.viadee.anchorj.server.test.resources;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.viadee.anchorj.server.model.CategoricalColumnSummary;
import de.viadee.anchorj.server.model.CategoryFreq;
import de.viadee.anchorj.server.model.ColumnSummary;
import de.viadee.anchorj.server.model.ContinuousColumnSummary;

/**
 *
 */
public class Resources {

    public static final String SIMPLE_CSV_FILE_STRING = "/csv/simple-csv-with-header.csv";

    public static final String AIRLINE_CSV = "/csv/airline-data.csv";

    public static final String AIRLINE_CLASSIFIER = "/mojo/GBM_Airlines_Classification.zip";

    public static final List<String> AIRLINE_FEATURES = Arrays.asList("Year", "Month", "DayofMonth", "DayOfWeek",
            "DepTime", "CRSDepTime", "ArrTime", "CRSArrTime", "UniqueCarrier", "FlightNum", "TailNum",
            "ActualElapsedTime", "CRSElapsedTime", "AirTime", "ArrDelay", "DepDelay", "Origin", "Dest", "Distance",
            "TaxiIn", "TaxiOut", "Cancelled", "CancellationCode", "Diverted", "CarrierDelay", "WeatherDelay",
            "NASDelay", "SecurityDelay", "LateAircraftDelay", "IsArrDelayed", "IsDepDelayed");

    public static final Map<String, Integer> AIRLINE_FEATURE_MAPPING = new HashMap<>(AIRLINE_FEATURES.size());

    public static final Serializable[] SIMPLE_AIRLINE_INSTANCE;

    public static final String TITANIC_CSV = "/csv/titanic_data.csv";

    public static final String TITANIC_CLASSIFIER = "/mojo/Titanic_GBM_grid_0_AutoML_20181207_120821_model_7.zip";

    public static final String[] SIMPLE_TITANIC_INSTANCE;

    public static final Map<String, Integer> TITANIC_FEATURE_MAPPING;

    public static final List<ColumnSummary<?>> TITANIC_COLUMN_SUMMARY;

    public static Path copyResource(final String resource) throws IOException {
        Path tempFile = Files.createTempFile("test_", ".csv");
        Files.copy(Resources.class.getResourceAsStream(resource), tempFile, StandardCopyOption.REPLACE_EXISTING);

        tempFile.toFile().deleteOnExit();
        return tempFile;

    }

    static {
        for (int i = 0; i < AIRLINE_FEATURES.size(); i++) {
            AIRLINE_FEATURE_MAPPING.put(AIRLINE_FEATURES.get(i), i);
        }

        SIMPLE_AIRLINE_INSTANCE = new Serializable[31];
        SIMPLE_AIRLINE_INSTANCE[0] = "1987";
        SIMPLE_AIRLINE_INSTANCE[1] = "10";
        SIMPLE_AIRLINE_INSTANCE[2] = "14";
        SIMPLE_AIRLINE_INSTANCE[3] = "3";
        SIMPLE_AIRLINE_INSTANCE[4] = "741";
        SIMPLE_AIRLINE_INSTANCE[5] = "730";
        SIMPLE_AIRLINE_INSTANCE[6] = "912";
        SIMPLE_AIRLINE_INSTANCE[7] = "849";
        SIMPLE_AIRLINE_INSTANCE[8] = "PS";
        SIMPLE_AIRLINE_INSTANCE[9] = "1451";
        SIMPLE_AIRLINE_INSTANCE[10] = "NA";
        SIMPLE_AIRLINE_INSTANCE[11] = "91";
        SIMPLE_AIRLINE_INSTANCE[12] = "79";
        SIMPLE_AIRLINE_INSTANCE[13] = null;
        SIMPLE_AIRLINE_INSTANCE[14] = "23";
        SIMPLE_AIRLINE_INSTANCE[15] = "11";
        SIMPLE_AIRLINE_INSTANCE[16] = "SAN";
        SIMPLE_AIRLINE_INSTANCE[17] = "SFO";
        SIMPLE_AIRLINE_INSTANCE[18] = "447";
        SIMPLE_AIRLINE_INSTANCE[19] = null;
        SIMPLE_AIRLINE_INSTANCE[20] = null;
        SIMPLE_AIRLINE_INSTANCE[21] = "0";
        SIMPLE_AIRLINE_INSTANCE[22] = "NA";
        SIMPLE_AIRLINE_INSTANCE[23] = "0";
        SIMPLE_AIRLINE_INSTANCE[24] = null;
        SIMPLE_AIRLINE_INSTANCE[25] = null;
        SIMPLE_AIRLINE_INSTANCE[26] = null;
        SIMPLE_AIRLINE_INSTANCE[27] = null;
        SIMPLE_AIRLINE_INSTANCE[28] = null;
        SIMPLE_AIRLINE_INSTANCE[29] = "YES";
        SIMPLE_AIRLINE_INSTANCE[30] = "YES";

        String FRAME_NAME = "titanic_train";
        TITANIC_COLUMN_SUMMARY = new ArrayList<>(15);
        TITANIC_FEATURE_MAPPING = new HashMap<>(15);

        TITANIC_COLUMN_SUMMARY.add(new ContinuousColumnSummary(FRAME_NAME, "PassengerId", "int", null, 0, 1,
                891, 446));
        TITANIC_FEATURE_MAPPING.put("PassengerId", 0);

        TITANIC_COLUMN_SUMMARY.add(new CategoricalColumnSummary<>(FRAME_NAME, "Survived", "enum", null,
                0, Arrays.asList(new CategoryFreq("0", 0.6161616161616161), new CategoryFreq("1", 0.3838383838383838)),
                2));
        TITANIC_FEATURE_MAPPING.put("Survived", 1);

        TITANIC_COLUMN_SUMMARY.add(new ContinuousColumnSummary(FRAME_NAME, "Pclass", "int", null,
                0, 1, 3, 2.3086419753086447));
        TITANIC_FEATURE_MAPPING.put("Pclass", 2);

        TITANIC_COLUMN_SUMMARY.add(new CategoricalColumnSummary<Double>(FRAME_NAME, "Name", "enum", null, 0, Arrays.asList(
                new CategoryFreq("Abbing, Mr. Anthony", 0.001122334455667789),
                new CategoryFreq("Abbott, Mr. Rossmore Edward", 0.001122334455667789),
                new CategoryFreq("Abbott, Mrs. Stanton (Rosa Hunt)", 0.001122334455667789),
                new CategoryFreq("Abelson, Mr. Samuel", 0.001122334455667789),
                new CategoryFreq("Abelson, Mrs. Samuel (Hannah Wizosky)", 0.001122334455667789)
        ), 21));
        TITANIC_FEATURE_MAPPING.put("Name", 3);

        TITANIC_COLUMN_SUMMARY.add(new CategoricalColumnSummary<Double>(FRAME_NAME, "Sex", "enum", null, 0, Arrays.asList(
                new CategoryFreq("male", 0.6475869809203143),
                new CategoryFreq("female", 0.35241301907968575)
        ), 2));
        TITANIC_FEATURE_MAPPING.put("Sex", 4);

        TITANIC_COLUMN_SUMMARY.add(new ContinuousColumnSummary(FRAME_NAME, "Age", "real", null,
                0, 0.42, 80, 29.630118193943826));
        TITANIC_FEATURE_MAPPING.put("Age", 5);

        TITANIC_COLUMN_SUMMARY.add(new ContinuousColumnSummary(FRAME_NAME, "SibSp", "int", null,
                0, 0, 8, 0.5230078563411893));
        TITANIC_FEATURE_MAPPING.put("SibSp", 6);

        TITANIC_COLUMN_SUMMARY.add(new ContinuousColumnSummary(FRAME_NAME, "Parch", "int", null,
                0, 0, 6, 0.3815937149270483));
        TITANIC_FEATURE_MAPPING.put("Parch", 7);

        TITANIC_COLUMN_SUMMARY.add(new CategoricalColumnSummary<Double>(FRAME_NAME, "Ticket", "enum", null,
                0, Arrays.asList(
                new CategoryFreq("1601", 0.007856341189674524),
                new CategoryFreq("347082", 0.007856341189674524),
                new CategoryFreq("CA. 2343", 0.007856341189674524),
                new CategoryFreq("3101295", 0.006734006734006734)
        ), 21));
        TITANIC_FEATURE_MAPPING.put("Ticket", 8);

        TITANIC_COLUMN_SUMMARY.add(new ContinuousColumnSummary(FRAME_NAME, "Fare", "real", null,
                0, 0, 512.3292, 32.20420796857465));
        TITANIC_FEATURE_MAPPING.put("Fare", 9);

        TITANIC_COLUMN_SUMMARY.add(new CategoricalColumnSummary<String>(FRAME_NAME, "Cabin", "string", null, 0, Arrays.asList(
                new CategoryFreq("", 0.7710437710437711),
                new CategoryFreq("B96 B98", 0.004489337822671156),
                new CategoryFreq("C23 C25 C27", 0.004489337822671156),
                new CategoryFreq("G6", 0.004489337822671156)
        ), 148));
        TITANIC_FEATURE_MAPPING.put("Cabin", 10);

        TITANIC_COLUMN_SUMMARY.add(new CategoricalColumnSummary<Double>(FRAME_NAME, "Embarked", "enum", null, 0, Arrays.asList(
                new CategoryFreq("S", 0.7227833894500562),
                new CategoryFreq("C", 0.18855218855218855),
                new CategoryFreq("Q", 0.08641975308641975),
                new CategoryFreq("", 0.002244668911335578)
        ), 4));
        TITANIC_FEATURE_MAPPING.put("Embarked", 11);

        TITANIC_COLUMN_SUMMARY.add(new CategoricalColumnSummary<Double>(FRAME_NAME, "rx_master", "enum", null, 0, Arrays.asList(
                new CategoryFreq("FALSE", 0.9551066217732884),
                new CategoryFreq("TRUE", 0.04489337822671156)
        ), 2));
        TITANIC_FEATURE_MAPPING.put("rx_master", 12);

        TITANIC_COLUMN_SUMMARY.add(new CategoricalColumnSummary<Double>(FRAME_NAME, "rx_miss", "enum", null, 0, Arrays.asList(
                new CategoryFreq("FALSE", 0.7957351290684624),
                new CategoryFreq("TRUE", 0.20426487093153758)
        ), 2));
        TITANIC_FEATURE_MAPPING.put("rx_miss", 13);

        TITANIC_COLUMN_SUMMARY.add(new ContinuousColumnSummary(FRAME_NAME, "CabinLength", "int", null,
                0, 0, 15, 0.8215488215488215));
        TITANIC_FEATURE_MAPPING.put("CabinLength", 14);

        SIMPLE_TITANIC_INSTANCE = new String[15];
        SIMPLE_TITANIC_INSTANCE[0] = "361";
        SIMPLE_TITANIC_INSTANCE[1] = "0";
        SIMPLE_TITANIC_INSTANCE[2] = "3";
        SIMPLE_TITANIC_INSTANCE[3] = "Skoog, Mr. Wilhelm";
        SIMPLE_TITANIC_INSTANCE[4] = "male";
        SIMPLE_TITANIC_INSTANCE[5] = "40.0";
        SIMPLE_TITANIC_INSTANCE[6] = "1";
        SIMPLE_TITANIC_INSTANCE[7] = "4";
        SIMPLE_TITANIC_INSTANCE[8] = "347088";
        SIMPLE_TITANIC_INSTANCE[9] = "27.9";
        SIMPLE_TITANIC_INSTANCE[10] = "";
        SIMPLE_TITANIC_INSTANCE[11] = "S";
        SIMPLE_TITANIC_INSTANCE[12] = "FALSE";
        SIMPLE_TITANIC_INSTANCE[13] = "FALSE";
        SIMPLE_TITANIC_INSTANCE[14] = "0";
    }
}
