package me.kroeker.alex.anchor.jsever.h2o.util;

import java.io.File;

/**
 *
 */
public class Resources {
    static final String SIMPLE_CSV_FILE_STRING = "csv/simple-csv-with-header.csv";

    static final File SIMPLE_CSV_FILE = new File(Resources.class.getClassLoader().getResource(Resources.SIMPLE_CSV_FILE_STRING).getFile());

    static final String AIRLINE_CLASSIFIER = "mojo/GBM_Airlines_Classification.zip";

}
