/*
 * This file is auto-generated by h2o-3/h2o-bindings/bin/gen_java.py
 * Copyright 2016 H2O.ai;  Apache License Version 2.0 (see LICENSE for details)
 */
package water.bindings.proxies.retrofit;

import water.bindings.pojos.*;
import retrofit2.*;
import retrofit2.http.*;
import okhttp3.ResponseBody;

public interface Parse {

  /** 
   * Parse a raw byte-oriented Frame into a useful columnar data Frame.
   *   @param destination_frame Final frame name
   *   @param source_frames Source frames
   *   @param parse_type Parser type
   *   @param separator Field separator
   *   @param single_quotes Single Quotes
   *   @param check_header Check header: 0 means guess, +1 means 1st line is header not data, -1 means 1st line is data
   *                       not header
   *   @param number_columns Number of columns
   *   @param column_names Column names
   *   @param column_types Value types for columns
   *   @param domains Domains for categorical columns
   *   @param na_strings NA strings for columns
   *   @param chunk_size Size of individual parse tasks
   *   @param delete_on_done Delete input key after parse
   *   @param blocking Block until the parse completes (as opposed to returning early and requiring polling
   *   @param decrypt_tool Key-reference to an initialized instance of a Decryption Tool
   *   @param _exclude_fields Comma-separated list of JSON field paths to exclude from the result, used like:
   *                          "/3/Frames?_exclude_fields=frames/frame_id/URL,__meta"
   */
  @FormUrlEncoded
  @POST("/3/Parse")
  Call<ParseV3> parse(
    @Field("destination_frame") String destination_frame,
    @Field("source_frames") String[] source_frames,
    @Field("parse_type") ApiParseTypeValuesProvider parse_type,
    @Field("separator") byte separator,
    @Field("single_quotes") boolean single_quotes,
    @Field("check_header") int check_header,
    @Field("number_columns") int number_columns,
    @Field("column_names") String[] column_names,
    @Field("column_types") String[] column_types,
    @Field("domains") String[][] domains,
    @Field("na_strings") String[][] na_strings,
    @Field("chunk_size") int chunk_size,
    @Field("delete_on_done") boolean delete_on_done,
    @Field("blocking") boolean blocking,
    @Field("decrypt_tool") String decrypt_tool,
    @Field("_exclude_fields") String _exclude_fields
  );

  @FormUrlEncoded
  @POST("/3/Parse")
  Call<ParseV3> parse(
    @Field("destination_frame") String destination_frame,
    @Field("source_frames") String[] source_frames
  );

}
