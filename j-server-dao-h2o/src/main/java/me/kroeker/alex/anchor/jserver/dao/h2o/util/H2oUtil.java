package me.kroeker.alex.anchor.jserver.dao.h2o.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.function.Consumer;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import okhttp3.ResponseBody;
import water.bindings.H2oApi;
import water.bindings.pojos.FrameKeyV3;

/**
 * @author ak902764
 */
public final class H2oUtil {
    private H2oUtil() {
    }

    public static boolean isEnumColumn(String columnType) {
        return "enum".equals(columnType);
    }

    public static boolean isStringColumn(String columnType) {
        return "string".equals(columnType) || "uuid".equals(columnType);
    }

    public static File downloadDataSet(FrameKeyV3 frameKey, H2oApi api) throws IOException {
        File dataSet = File.createTempFile("h2o_data_set", ".csv");
        ResponseBody data = api._downloadDataset_fetch(frameKey);
        FileUtils.copyInputStreamToFile(data.byteStream(), dataSet);

        return dataSet;
    }

    public static void iterateThroughCsvData(File file, Consumer<CSVRecord> recordConsumer) throws IOException {
        try (Reader in = new FileReader(file)) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);
            for (CSVRecord record : records) {
                recordConsumer.accept(record);
            }
        }
    }

}
