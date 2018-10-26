/*
 * This file is auto-generated by h2o-3/h2o-bindings/bin/gen_java.py
 * Copyright 2016 H2O.ai;  Apache License Version 2.0 (see LICENSE for details)
 */
package water.bindings.proxies.retrofit;

import water.bindings.pojos.*;
import retrofit2.*;
import retrofit2.http.*;
import okhttp3.ResponseBody;

public interface Modelsinfo {

  /** 
   * Return basic information about all models available to train.
   *   @param __schema Url describing the schema of the current object.
   */
  @GET("/4/modelsinfo")
  Call<ModelsInfoV4> modelsInfo(@Query("__schema") String __schema);

  @GET("/4/modelsinfo")
  Call<ModelsInfoV4> modelsInfo();

}