package de.viadee.anchorj.server.h2o.util;

import okhttp3.ResponseBody;
import retrofit2.Response;
import water.bindings.H2oApi;
import water.bindings.pojos.ModelKeyV3;

import java.io.IOException;

public class H2oMojoDownload extends H2oDownload {

    @Override
    protected Response<ResponseBody> callRest(H2oApi api, String key) throws IOException {
        ModelKeyV3 modelKey = new ModelKeyV3();
        modelKey.name = key;

        return api.modelMojo(modelKey);
    }

}
