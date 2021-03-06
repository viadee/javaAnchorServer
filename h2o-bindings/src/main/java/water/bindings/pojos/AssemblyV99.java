/*
 * This file is auto-generated by h2o-3/h2o-bindings/bin/gen_java.py
 * Copyright 2016 H2O.ai;  Apache License Version 2.0 (see LICENSE for details)
 */
package water.bindings.pojos;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.*;


public class AssemblyV99 extends RequestSchemaV3 {

    /**
     * A list of steps describing the assembly line.
     */
    public String[] steps;

    /**
     * Input Frame for the assembly.
     */
    public FrameKeyV3 frame;

    /**
     * The name of the file and generated class
     */
    @SerializedName("pojo_name")
    public String pojoName;

    /**
     * The key of the Assembly object to retrieve from the DKV.
     */
    @SerializedName("assembly_id")
    public String assemblyId;

    /**
     * Output of the assembly line.
     */
    public FrameKeyV3 result;

    /**
     * A Key to the fit Assembly data structure
     */
    public AssemblyKeyV3 assembly;


    /*------------------------------------------------------------------------------------------------------------------
    //                                                  INHERITED
    //------------------------------------------------------------------------------------------------------------------

    // Comma-separated list of JSON field paths to exclude from the result, used like:
    // "/3/Frames?_exclude_fields=frames/frame_id/URL,__meta"
    public String _excludeFields;

    */

    /**
     * Public constructor
     */
    public AssemblyV99() {
        pojoName = "";
        assemblyId = "";
        _excludeFields = "";
    }

    /**
     * Return the contents of this object as a JSON String.
     */
    @Override
    public String toString() {
        return new GsonBuilder().serializeSpecialFloatingPointValues().create().toJson(this);
    }

}
