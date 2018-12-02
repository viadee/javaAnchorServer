package de.viadee.anchorj.server.anchor.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.viadee.anchorj.server.model.AnchorConfigDescription;

/**
 *
 */
public class AnchorConfig {

    private static final String ANCHOR_TAU = "Tau";
    //    private static final String ANCHOR_DELTA = "Delta";
//    private static final String ANCHOR_EPSILON = "Epsilon";
    private static final String ANCHOR_TAU_DISCREPANCY = "Tau-Discrepancy";
    private static final String ANCHOR_BUCKET_NO = "Bucket-No.";
    //    private static final String SP_SAMPLE_SIZE = "Sample-Size";
    private static final String SP_NO_ANCHOR = "No-Anchor";

    private static final Map<String, AnchorConfigDescription> DEFAULT_ANCHOR_PARAMS;

    static {
        DEFAULT_ANCHOR_PARAMS = new HashMap<>();
        DEFAULT_ANCHOR_PARAMS.put(ANCHOR_TAU, new AnchorConfigDescription(ANCHOR_TAU,
                AnchorConfigDescription.ConfigInputType.DOUBLE, 0.9)
        );
//        DEFAULT_ANCHOR_PARAMS.put(ANCHOR_DELTA, new AnchorConfigDescription(ANCHOR_DELTA,
//                AnchorConfigDescription.ConfigInputType.DOUBLE, 0.1)
//        );
//        DEFAULT_ANCHOR_PARAMS.put(ANCHOR_EPSILON, new AnchorConfigDescription(ANCHOR_EPSILON,
//                AnchorConfigDescription.ConfigInputType.DOUBLE, 0.1)
//        );
        DEFAULT_ANCHOR_PARAMS.put(ANCHOR_TAU_DISCREPANCY, new AnchorConfigDescription(ANCHOR_TAU_DISCREPANCY,
                AnchorConfigDescription.ConfigInputType.DOUBLE, 0.05)
        );
        DEFAULT_ANCHOR_PARAMS.put(ANCHOR_BUCKET_NO, new AnchorConfigDescription(ANCHOR_BUCKET_NO,
                AnchorConfigDescription.ConfigInputType.INTEGER, 5)
        );
//        DEFAULT_ANCHOR_PARAMS.put(SP_SAMPLE_SIZE, new AnchorConfigDescription(SP_SAMPLE_SIZE,
//                AnchorConfigDescription.ConfigInputType.INTEGER, Integer.MAX_VALUE)
//        );
        DEFAULT_ANCHOR_PARAMS.put(SP_NO_ANCHOR, new AnchorConfigDescription(SP_NO_ANCHOR,
                AnchorConfigDescription.ConfigInputType.INTEGER, 3)
        );
    }

    public static Collection<AnchorConfigDescription> getAnchorConfigs() {
        return DEFAULT_ANCHOR_PARAMS.values();
    }

    private static Object getAnchorOptionFromParamsOrDefault(Map<String, Object> anchorConfig, String paramName) {
        if (anchorConfig == null) {
            anchorConfig = new HashMap<>();
        }
        return anchorConfig.getOrDefault(paramName, DEFAULT_ANCHOR_PARAMS.get(paramName).getValue());
    }

    public static Integer getBucketNo(Map<String, Object> anchorConfig) {
        return (Integer) getAnchorOptionFromParamsOrDefault(anchorConfig, ANCHOR_BUCKET_NO);
    }

    public static Integer getSpAnchorNo(Map<String, Object> anchorConfig) {
        return (Integer) getAnchorOptionFromParamsOrDefault(anchorConfig, SP_NO_ANCHOR);
    }

    public static Double getTau(Map<String, Object> anchorConfig) {
        return (Double) getAnchorOptionFromParamsOrDefault(anchorConfig, ANCHOR_TAU);
    }

    public static Double getTauDiscrepancy(Map<String, Object> anchorConfig) {
        return (Double) getAnchorOptionFromParamsOrDefault(anchorConfig, ANCHOR_TAU_DISCREPANCY);
    }

}
