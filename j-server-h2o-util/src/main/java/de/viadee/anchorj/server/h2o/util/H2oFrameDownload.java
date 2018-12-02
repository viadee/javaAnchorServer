package de.viadee.anchorj.server.h2o.util;

import okhttp3.ResponseBody;
import retrofit2.Response;
import water.bindings.H2oApi;

import java.io.IOException;

public class H2oFrameDownload extends H2oDownload {

    @Override
    protected Response<ResponseBody> callRest(H2oApi api, String key) throws IOException {
        return api._downloadDataset_fetch(H2oApi.stringToFrameKey(key));
    }

}
