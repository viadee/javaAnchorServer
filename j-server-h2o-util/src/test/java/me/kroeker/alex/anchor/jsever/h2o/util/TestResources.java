package me.kroeker.alex.anchor.jsever.h2o.util;

import java.io.File;

/**
 */
public class TestResources {
    static final String SIMPLE_CSV_FILE_STRING = "csv/simple-csv-with-header.csv";

    static final File SIMPLE_CSV_FILE = new File(TestResources.class.getClassLoader().getResource(TestResources.SIMPLE_CSV_FILE_STRING).getFile());
}
