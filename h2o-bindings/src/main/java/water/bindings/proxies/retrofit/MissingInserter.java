/*
 * This file is auto-generated by h2o-3/h2o-bindings/bin/gen_java.py
 * Copyright 2016 H2O.ai;  Apache License Version 2.0 (see LICENSE for details)
 */
package water.bindings.proxies.retrofit;

import water.bindings.pojos.*;
import retrofit2.*;
import retrofit2.http.*;
import okhttp3.ResponseBody;

public interface MissingInserter {

  /** 
   * Insert missing values.
   *   @param dataset dataset
   *   @param fraction Fraction of data to replace with a missing value
   *   @param seed Seed
   *   @param _exclude_fields Comma-separated list of JSON field paths to exclude from the result, used like:
   *                          "/3/Frames?_exclude_fields=frames/frame_id/URL,__meta"
   */
  @FormUrlEncoded
  @POST("/3/MissingInserter")
  Call<JobV3> run(
    @Field("dataset") String dataset,
    @Field("fraction") double fraction,
    @Field("seed") long seed,
    @Field("_exclude_fields") String _exclude_fields
  );

  @FormUrlEncoded
  @POST("/3/MissingInserter")
  Call<JobV3> run(
    @Field("dataset") String dataset,
    @Field("fraction") double fraction
  );

}
