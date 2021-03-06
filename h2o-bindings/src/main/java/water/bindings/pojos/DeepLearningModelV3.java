/*
 * This file is auto-generated by h2o-3/h2o-bindings/bin/gen_java.py
 * Copyright 2016 H2O.ai;  Apache License Version 2.0 (see LICENSE for details)
 */
package water.bindings.pojos;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.*;


public class DeepLearningModelV3 extends ModelSchemaV3<DeepLearningParametersV3, DeepLearningModelOutputV3> {


    /*------------------------------------------------------------------------------------------------------------------
    //                                                  INHERITED
    //------------------------------------------------------------------------------------------------------------------

    // The build parameters for the model (e.g. K for KMeans).
    public DeepLearningParametersV3 parameters;

    // The build output for the model (e.g. the cluster centers for KMeans).
    public DeepLearningModelOutputV3 output;

    // Compatible frames, if requested
    public String[] compatibleFrames;

    // Checksum for all the things that go into building the Model.
    public long checksum;

    // Model key
    public ModelKeyV3 modelId;

    // The algo name for this Model.
    public String algo;

    // The pretty algo name for this Model (e.g., Generalized Linear Model, rather than GLM).
    public String algoFullName;

    // The response column name for this Model (if applicable). Is null otherwise.
    public String responseColumnName;

    // The Model's training frame key
    public FrameKeyV3 dataFrame;

    // Timestamp for when this model was completed
    public long timestamp;

    // Indicator, whether export to POJO is available
    public boolean havePojo;

    // Indicator, whether export to MOJO is available
    public boolean haveMojo;

    */

    /**
     * Public constructor
     */
    public DeepLearningModelV3() {
        checksum = 0L;
        algo = "";
        algoFullName = "";
        responseColumnName = "";
        timestamp = 0L;
        havePojo = false;
        haveMojo = false;
    }

    /**
     * Return the contents of this object as a JSON String.
     */
    @Override
    public String toString() {
        return new GsonBuilder().serializeSpecialFloatingPointValues().create().toJson(this);
    }

}
