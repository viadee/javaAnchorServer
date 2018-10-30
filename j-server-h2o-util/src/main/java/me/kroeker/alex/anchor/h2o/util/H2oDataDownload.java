package me.kroeker.alex.anchor.h2o.util;

import okhttp3.ResponseBody;
import org.apache.commons.io.FileUtils;
import water.bindings.H2oApi;
import water.bindings.pojos.FrameKeyV3;

import java.io.File;
import java.io.IOException;

public class H2oDataDownload implements AutoCloseable {

    private File dataSet;

    public File getFile(H2oApi api, FrameKeyV3 frameKey) throws IOException {
        dataSet = File.createTempFile("h2o_data_set", ".csv");
        ResponseBody data = api._downloadDataset_fetch(frameKey);
        FileUtils.copyInputStreamToFile(data.byteStream(), dataSet);

        return dataSet;
    }

    @Override
    public void close() throws IOException {
        if (this.dataSet != null && !this.dataSet.delete()) {
            throw new IOException("Failed to delete downloaded data set, try delete on exit: " + dataSet.getAbsolutePath());
        }
    }
}
