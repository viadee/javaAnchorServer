/*
 * This file is auto-generated by h2o-3/h2o-bindings/bin/gen_java.py
 * Copyright 2016 H2O.ai;  Apache License Version 2.0 (see LICENSE for details)
 */
package water.bindings.pojos;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.*;


public class ModelMetricsBaseV3 extends SchemaV3 {

    /**
     * The model used for this scoring run.
     */
    public ModelKeyV3 model;

    /**
     * The checksum for the model used for this scoring run.
     */
    @SerializedName("model_checksum")
    public long modelChecksum;

    /**
     * The frame used for this scoring run.
     */
    public FrameKeyV3 frame;

    /**
     * The checksum for the frame used for this scoring run.
     */
    @SerializedName("frame_checksum")
    public long frameChecksum;

    /**
     * Optional description for this scoring run (to note out-of-bag, sampled data, etc.)
     */
    public String description;

    /**
     * The category (e.g., Clustering) for the model used for this scoring run.
     */
    @SerializedName("model_category")
    public ModelCategory modelCategory;

    /**
     * The time in mS since the epoch for the start of this scoring run.
     */
    @SerializedName("scoring_time")
    public long scoringTime;

    /**
     * Predictions Frame.
     */
    public FrameV3 predictions;

    /**
     * The Mean Squared Error of the prediction for this scoring run.
     */
    @SerializedName("MSE")
    public double mse;

    /**
     * The Root Mean Squared Error of the prediction for this scoring run.
     */
    @SerializedName("RMSE")
    public double rmse;

    /**
     * Number of observations.
     */
    public long nobs;

    /**
     * Name of custom metric
     */
    @SerializedName("custom_metric_name")
    public String customMetricName;

    /**
     * Value of custom metric
     */
    @SerializedName("custom_metric_value")
    public double customMetricValue;

    /**
     * Public constructor
     */
    public ModelMetricsBaseV3() {
        modelChecksum = 0L;
        frameChecksum = 0L;
        description = "";
        scoringTime = 0L;
        mse = 0.0;
        rmse = 0.0;
        nobs = 0L;
        customMetricName = "";
        customMetricValue = 0.0;
    }

    /**
     * Return the contents of this object as a JSON String.
     */
    @Override
    public String toString() {
        return new GsonBuilder().serializeSpecialFloatingPointValues().create().toJson(this);
    }

}
