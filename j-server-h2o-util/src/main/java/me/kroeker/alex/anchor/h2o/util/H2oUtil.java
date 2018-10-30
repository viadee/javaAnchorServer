package me.kroeker.alex.anchor.h2o.util;

import okhttp3.ResponseBody;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import water.bindings.H2oApi;
import water.bindings.pojos.FrameKeyV3;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author ak902764
 */
public final class H2oUtil {

    static final Map<String, String> H2O_SERVER = new HashMap<>();

    // TODO make list of servers configurable
    static {
        H2O_SERVER.put("local-H2O", "http://localhost:54321");
    }

    private H2oUtil() {
    }

    public static Collection<String> getH2oConnectionNames() {
        return H2O_SERVER.keySet();
    }

    public static H2oApi createH2o(String connectionName) {
        return new H2oApi(H2O_SERVER.get(connectionName));
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

}
