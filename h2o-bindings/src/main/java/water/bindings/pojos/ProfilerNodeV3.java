/*
 * This file is auto-generated by h2o-3/h2o-bindings/bin/gen_java.py
 * Copyright 2016 H2O.ai;  Apache License Version 2.0 (see LICENSE for details)
 */
package water.bindings.pojos;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.*;


public class ProfilerNodeV3 extends SchemaV3 {

    /**
     * Node names
     */
    @SerializedName("node_name")
    public String nodeName;

    /**
     * Timestamp (millis since epoch)
     */
    public long timestamp;

    /**
     * Profile entry list
     */
    public ProfilerNodeEntryV3[] entries;

    /**
     * Public constructor
     */
    public ProfilerNodeV3() {
        nodeName = "";
        timestamp = 0L;
    }

    /**
     * Return the contents of this object as a JSON String.
     */
    @Override
    public String toString() {
        return new GsonBuilder().serializeSpecialFloatingPointValues().create().toJson(this);
    }

}