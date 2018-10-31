package me.kroeker.alex.anchor.h2o.util;

import okhttp3.ResponseBody;
import org.apache.commons.io.FileUtils;
import water.bindings.H2oApi;

import java.io.File;
import java.io.IOException;

public abstract class H2oDownload implements AutoCloseable {

    private File file;

    public H2oDownload() {
    }

    public File getFile(H2oApi api, String key) throws IOException {
        file = File.createTempFile("h2o_data_set", ".csv");
        ResponseBody data = this.callRest(api, key);
        FileUtils.copyInputStreamToFile(data.byteStream(), file);

        return file;
    }

    @Override
    public void close() throws IOException {
        if (this.file != null && !this.file.delete()) {
            throw new IOException("Failed to delete downloaded data set, try delete on exit: " + file.getAbsolutePath());
        }
    }

    protected abstract ResponseBody callRest(H2oApi api, String key) throws IOException;
}
