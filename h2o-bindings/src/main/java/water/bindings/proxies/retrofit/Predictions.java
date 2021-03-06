/*
 * This file is auto-generated by h2o-3/h2o-bindings/bin/gen_java.py
 * Copyright 2016 H2O.ai;  Apache License Version 2.0 (see LICENSE for details)
 */
package water.bindings.proxies.retrofit;

import water.bindings.pojos.*;
import retrofit2.*;
import retrofit2.http.*;
import okhttp3.ResponseBody;

public interface Predictions {

  /** 
   * Score (generate predictions) for the specified Frame with the specified Model.  Both the Frame of predictions and
   * the metrics will be returned.
   *   @param model Key of Model of interest (optional)
   *   @param frame Key of Frame of interest (optional)
   *   @param predictions_frame Key of predictions frame, if predictions are requested (optional)
   *   @param deviances_frame Key for the frame containing per-observation deviances (optional)
   *   @param reconstruction_error Compute reconstruction error (optional, only for Deep Learning AutoEncoder models)
   *   @param reconstruction_error_per_feature Compute reconstruction error per feature (optional, only for Deep
   *                                           Learning AutoEncoder models)
   *   @param deep_features_hidden_layer Extract Deep Features for given hidden layer (optional, only for Deep Learning
   *                                     models)
   *   @param deep_features_hidden_layer_name Extract Deep Features for given hidden layer by name (optional, only for
   *                                          Deep Water models)
   *   @param reconstruct_train Reconstruct original training frame (optional, only for GLRM models)
   *   @param project_archetypes Project GLRM archetypes back into original feature space (optional, only for GLRM
   *                             models)
   *   @param reverse_transform Reverse transformation applied during training to model output (optional, only for GLRM
   *                            models)
   *   @param leaf_node_assignment Return the leaf node assignment (optional, only for DRF/GBM models)
   *   @param leaf_node_assignment_type Type of the leaf node assignment (optional, only for DRF/GBM models)
   *   @param predict_staged_proba Predict the class probabilities at each stage (optional, only for GBM models)
   *   @param exemplar_index Retrieve all members for a given exemplar (optional, only for Aggregator models)
   *   @param deviances Compute the deviances per row (optional, only for classification or regression models)
   *   @param custom_metric_func Reference to custom evaluation function, format: `language:keyName=funcName`
   *   @param _exclude_fields Comma-separated list of JSON field paths to exclude from the result, used like:
   *                          "/3/Frames?_exclude_fields=frames/frame_id/URL,__meta"
   */
  @FormUrlEncoded
  @POST("/3/Predictions/models/{model}/frames/{frame}")
  Call<ModelMetricsListSchemaV3> predict(
    @Path("model") String model,
    @Path("frame") String frame,
    @Field("predictions_frame") String predictions_frame,
    @Field("deviances_frame") String deviances_frame,
    @Field("reconstruction_error") boolean reconstruction_error,
    @Field("reconstruction_error_per_feature") boolean reconstruction_error_per_feature,
    @Field("deep_features_hidden_layer") int deep_features_hidden_layer,
    @Field("deep_features_hidden_layer_name") String deep_features_hidden_layer_name,
    @Field("reconstruct_train") boolean reconstruct_train,
    @Field("project_archetypes") boolean project_archetypes,
    @Field("reverse_transform") boolean reverse_transform,
    @Field("leaf_node_assignment") boolean leaf_node_assignment,
    @Field("leaf_node_assignment_type") ModelLeafNodeAssignmentLeafNodeAssignmentType leaf_node_assignment_type,
    @Field("predict_staged_proba") boolean predict_staged_proba,
    @Field("exemplar_index") int exemplar_index,
    @Field("deviances") boolean deviances,
    @Field("custom_metric_func") String custom_metric_func,
    @Field("_exclude_fields") String _exclude_fields
  );

  @FormUrlEncoded
  @POST("/3/Predictions/models/{model}/frames/{frame}")
  Call<ModelMetricsListSchemaV3> predict(
    @Path("model") String model,
    @Path("frame") String frame
  );

  /** 
   * Score (generate predictions) for the specified Frame with the specified Model.  Both the Frame of predictions and
   * the metrics will be returned.
   *   @param model Key of Model of interest (optional)
   *   @param frame Key of Frame of interest (optional)
   *   @param predictions_frame Key of predictions frame, if predictions are requested (optional)
   *   @param deviances_frame Key for the frame containing per-observation deviances (optional)
   *   @param reconstruction_error Compute reconstruction error (optional, only for Deep Learning AutoEncoder models)
   *   @param reconstruction_error_per_feature Compute reconstruction error per feature (optional, only for Deep
   *                                           Learning AutoEncoder models)
   *   @param deep_features_hidden_layer Extract Deep Features for given hidden layer (optional, only for Deep Learning
   *                                     models)
   *   @param deep_features_hidden_layer_name Extract Deep Features for given hidden layer by name (optional, only for
   *                                          Deep Water models)
   *   @param reconstruct_train Reconstruct original training frame (optional, only for GLRM models)
   *   @param project_archetypes Project GLRM archetypes back into original feature space (optional, only for GLRM
   *                             models)
   *   @param reverse_transform Reverse transformation applied during training to model output (optional, only for GLRM
   *                            models)
   *   @param leaf_node_assignment Return the leaf node assignment (optional, only for DRF/GBM models)
   *   @param leaf_node_assignment_type Type of the leaf node assignment (optional, only for DRF/GBM models)
   *   @param predict_staged_proba Predict the class probabilities at each stage (optional, only for GBM models)
   *   @param exemplar_index Retrieve all members for a given exemplar (optional, only for Aggregator models)
   *   @param deviances Compute the deviances per row (optional, only for classification or regression models)
   *   @param custom_metric_func Reference to custom evaluation function, format: `language:keyName=funcName`
   *   @param _exclude_fields Comma-separated list of JSON field paths to exclude from the result, used like:
   *                          "/3/Frames?_exclude_fields=frames/frame_id/URL,__meta"
   */
  @FormUrlEncoded
  @POST("/4/Predictions/models/{model}/frames/{frame}")
  Call<JobV3> predictAsync(
    @Path("model") String model,
    @Path("frame") String frame,
    @Field("predictions_frame") String predictions_frame,
    @Field("deviances_frame") String deviances_frame,
    @Field("reconstruction_error") boolean reconstruction_error,
    @Field("reconstruction_error_per_feature") boolean reconstruction_error_per_feature,
    @Field("deep_features_hidden_layer") int deep_features_hidden_layer,
    @Field("deep_features_hidden_layer_name") String deep_features_hidden_layer_name,
    @Field("reconstruct_train") boolean reconstruct_train,
    @Field("project_archetypes") boolean project_archetypes,
    @Field("reverse_transform") boolean reverse_transform,
    @Field("leaf_node_assignment") boolean leaf_node_assignment,
    @Field("leaf_node_assignment_type") ModelLeafNodeAssignmentLeafNodeAssignmentType leaf_node_assignment_type,
    @Field("predict_staged_proba") boolean predict_staged_proba,
    @Field("exemplar_index") int exemplar_index,
    @Field("deviances") boolean deviances,
    @Field("custom_metric_func") String custom_metric_func,
    @Field("_exclude_fields") String _exclude_fields
  );

  @FormUrlEncoded
  @POST("/4/Predictions/models/{model}/frames/{frame}")
  Call<JobV3> predictAsync(
    @Path("model") String model,
    @Path("frame") String frame
  );

}
