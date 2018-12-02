package de.viadee.anchorj.server.h2o.util;

import okhttp3.ResponseBody;
import org.apache.commons.io.FileUtils;
import retrofit2.Response;
import water.bindings.H2oApi;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;

public abstract class H2oDownload implements AutoCloseable {

    private File file;

    public H2oDownload() {
    }

    public File getFile(H2oApi api, String key) throws IOException {
        key = URLEncoder.encode(key, Charset.defaultCharset().name());

        file = File.createTempFile("h2o_data_set", ".csv");
        Response<ResponseBody> response = this.callRest(api, key);
        if (response.isSuccessful()) {
            FileUtils.copyInputStreamToFile(response.body().byteStream(), file);
        } else {
            // TODO handle errors for every h2o request
        }

        return file;
    }

    @Override
    public void close() throws IOException {
        if (this.file != null && !this.file.delete()) {
            throw new IOException("Failed to delete downloaded data set, try delete on exit: " + file.getAbsolutePath());
        }
    }

    protected abstract Response<ResponseBody> callRest(H2oApi api, String key) throws IOException;
}
