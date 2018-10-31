package me.kroeker.alex.anchor.h2o.util;

import okhttp3.ResponseBody;
import water.bindings.H2oApi;

import java.io.IOException;

public class H2oFrameDownload extends H2oDownload {

    @Override
    protected ResponseBody callRest(H2oApi api, String key) throws IOException {
        return api._downloadDataset_fetch(H2oApi.stringToFrameKey(key));
    }

}
