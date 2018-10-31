package me.kroeker.alex.anchor.h2o.util;

import okhttp3.ResponseBody;
import water.bindings.H2oApi;
import water.bindings.pojos.ModelKeyV3;

import java.io.IOException;

public class H2oMojoDownload extends H2oDownload {

    @Override
    protected ResponseBody callRest(H2oApi api, String key) throws IOException {
        ModelKeyV3 modelKey = new ModelKeyV3();
        modelKey.name = key;

        return api.modelMojo(modelKey);
    }

}
