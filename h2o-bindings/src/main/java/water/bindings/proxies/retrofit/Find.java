/*
 * This file is auto-generated by h2o-3/h2o-bindings/bin/gen_java.py
 * Copyright 2016 H2O.ai;  Apache License Version 2.0 (see LICENSE for details)
 */
package water.bindings.proxies.retrofit;

import water.bindings.pojos.*;
import retrofit2.*;
import retrofit2.http.*;
import okhttp3.ResponseBody;

public interface Find {

  /** 
   * Find a value within a Frame.
   *   @param key Frame to search
   *   @param column Column, or null for all
   *   @param row Starting row for search
   *   @param match Value to search for; leave blank for a search for missing values
   *   @param _exclude_fields Comma-separated list of JSON field paths to exclude from the result, used like:
   *                          "/3/Frames?_exclude_fields=frames/frame_id/URL,__meta"
   */
  @GET("/3/Find")
  Call<FindV3> find(
    @Query("key") FrameV3 key,
    @Query("column") String column,
    @Query("row") long row,
    @Query("match") String match,
    @Query("_exclude_fields") String _exclude_fields
  );

  @GET("/3/Find")
  Call<FindV3> find(
    @Query("key") FrameV3 key,
    @Query("row") long row
  );

}