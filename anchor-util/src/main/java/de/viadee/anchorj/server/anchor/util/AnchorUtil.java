package de.viadee.anchorj.server.anchor.util;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import de.viadee.anchorj.AnchorCandidate;
import de.viadee.anchorj.AnchorResult;
import de.viadee.anchorj.DataInstance;
import de.viadee.anchorj.h2o.H2oTabularNominalMojoClassifier;
import de.viadee.anchorj.server.h2o.util.H2oUtil;
import de.viadee.anchorj.server.model.Anchor;
import de.viadee.anchorj.server.model.AnchorPredicate;
import de.viadee.anchorj.server.model.ColumnSummary;
import de.viadee.anchorj.tabular.AnchorTabular;
import de.viadee.anchorj.tabular.TabularInstance;
import de.viadee.anchorj.tabular.column.GenericColumn;
import de.viadee.anchorj.tabular.column.IgnoredColumn;
import de.viadee.anchorj.tabular.column.IntegerColumn;
import de.viadee.anchorj.tabular.column.StringColumn;
import de.viadee.anchorj.tabular.discretizer.DiscretizerRelation;
import de.viadee.anchorj.tabular.discretizer.PercentileMedianDiscretizer;
import de.viadee.anchorj.tabular.discretizer.UniqueValueDiscretizer;
import de.viadee.anchorj.tabular.transformations.ReplaceNullTransformer;
import de.viadee.anchorj.tabular.transformations.StringToDoubleTransformer;
import de.viadee.anchorj.tabular.transformations.StringToIntTransformer;

/**
 *
 */
@SuppressWarnings("WeakerAccess")
public class AnchorUtil {

    private AnchorUtil() {
    }

    public static <T extends TabularInstance> void calculateCoveragePerPredicate(final T[] instances,
                                                                                 final Collection<Anchor> explanations) {
        final Consumer<AnchorPredicate> calculateCoveragePerPredicate = predicate -> {
            final Integer featureValue = predicate.getDiscretizedValue();
            final Set<T> coveredInstances = new HashSet<>();
            Stream.of(instances).parallel().forEach(item -> {
                if (item.getValue(predicate.getFeatureName()).equals(featureValue)) {
                    coveredInstances.add(item);
                }
            });
            double exactCoverage = (double) coveredInstances.size() / instances.length;
            predicate.setExactCoverage(exactCoverage);
        };

        explanations.forEach((expl) -> expl.getPredicates().values().forEach(calculateCoveragePerPredicate));

    }

    public static <T extends TabularInstance, E extends AnchorResult<T>> Set<T> findCoveredInstances(final T[] instances,
                                                                                                     final List<E> anchorResults) {
        // TODO the final may be the reason of the odd behavior of the unit test
        final Set<T> coveredInstances = new HashSet<>();
        Consumer<T> findCoveredInstance = item ->
                anchorResults.forEach((result) -> {
                    if (isInstanceInAnchor(item, result.getInstance(), result.getOrderedFeatures())) {
                        coveredInstances.add(item);
                    }
                });

        Stream.of(instances).parallel().forEach(findCoveredInstance);

        return coveredInstances;
    }

    public static <T extends DataInstance<?>> boolean isInstanceInAnchor(T item, T comparingInstance, List<Integer> featureIndexes) {
        for (Integer featureIndex : featureIndexes) {
            if (!item.getValue(featureIndex).equals(comparingInstance.getValue(featureIndex))) {
                return false;
            }
        }
        return true;
    }

    public static Anchor transformAnchor(String modelId,
                                         String frameId,
                                         int dataSetSize,
                                         AnchorTabular anchor,
                                         H2oTabularNominalMojoClassifier<TabularInstance> classificationFunction,
                                         AnchorResultWithExactCoverage anchorResult) {
        Anchor convertedAnchor = new Anchor();
        convertedAnchor.setCoverage(anchorResult.getCoverage());
        convertedAnchor.setPrecision(anchorResult.getPrecision());
        convertedAnchor.setCreated_at(LocalDateTime.now());
        convertedAnchor.setFeatures(anchorResult.getOrderedFeatures());
        convertedAnchor.setFrame_id(frameId);
        convertedAnchor.setModel_id(modelId);

        TabularInstance cleanedInstance = anchorResult.getInstance();
        Serializable labelOfCase = cleanedInstance.getTransformedLabel();
        convertedAnchor.setLabel_of_case(labelOfCase);

        Map<String, Serializable> cleanedInstanceMap = new HashMap<>(cleanedInstance.getFeatureCount());
        for (int i = 0; i < cleanedInstance.getFeatureCount(); i++) {
            cleanedInstanceMap.put(anchor.getColumns().get(i).getName(), cleanedInstance.getTransformedValue(i));
        }
        convertedAnchor.setInstance(cleanedInstanceMap);

        final int affectedRows = (int) Math.round(dataSetSize * convertedAnchor.getCoverage());
        convertedAnchor.setAffected_rows(affectedRows);

        String prediction = classificationFunction.getModelWrapper().getResponseDomainValues()[anchorResult.getLabel()];
        convertedAnchor.setPrediction(prediction);

        final Map<Integer, AnchorPredicate> anchorPredicateMap = new HashMap<>();
        createAnchorPredicateMap(anchorPredicateMap, anchorResult, anchorResult.getInstance());
        convertedAnchor.setPredicates(anchorPredicateMap);
//        for (Map.Entry<Integer, FeatureValueMapping> entry : anchor.getVisualizer().getAnchor(anchorResult).entrySet()) {
//            final TabularFeature feature = entry.getValue().getFeature();
//
//            final int featureIndex = anchorResult.getInstance().getFeatureArrayIndex(feature.getName());
//            final AnchorCandidate candidate = AnchorUtil.findCandidate(anchorResult, featureIndex);
//            double addedCoverage = 0;
//            double addedPrecision = 0;
//            if (candidate != null) {
//                addedCoverage = candidate.getAddedCoverage();
//                addedPrecision = candidate.getAddedPrecision();
//            } else {
//                LOG.error("No AnchorCandidate for feature with index " + featureIndex + " and name " + feature.getName() + " found");
//            }
//
//            final FeatureValueMapping featureValueMapping = entry.getValue();
//            if (featureValueMapping instanceof CategoricalValueMapping) {
//                String value = featureValueMapping.getValue().toString();
//                enumAnchors.put(entry.getKey(), new AnchorPredicateEnum(feature.getName(), (Integer) ((CategoricalValueMapping) featureValueMapping).getCategoricalValue(), value, addedPrecision, addedCoverage));
//            } else if (featureValueMapping instanceof NativeValueMapping) {
//                enumAnchors.put(entry.getKey(), new AnchorPredicateEnum(feature.getName(), (Integer) featureValueMapping.getValue(),
//                        featureValueMapping.getValue().toString(), addedPrecision, addedCoverage));
//            } else if (featureValueMapping instanceof MetricValueMapping) {
//                MetricValueMapping metric = (MetricValueMapping) featureValueMapping;
//                metricAnchors.put(entry.getKey(), new AnchorPredicateMetric(feature.getName(), (Integer) featureValueMapping.getValue(),
//                        addedPrecision, addedCoverage, metric.getMinValue(), metric.getMaxValue()));
//            } else {
//                throw new IllegalArgumentException("feature value mapping of type " +
//                        featureValueMapping.getClass().getSimpleName() + " not handled");
//            }
//        }
//        convertedAnchor.setEnumPredicate(enumAnchors);
//        convertedAnchor.setMetricPredicate(metricAnchors);
        return convertedAnchor;
    }

    public static void createAnchorPredicateMap(Map<Integer, AnchorPredicate> predicateMap, AnchorCandidate candidate,
                                                TabularInstance instance) {
        if (candidate == null) {
            return;
        }

        int featureIndex = candidate.getOrderedFeatures().get(0);
        GenericColumn featureOfInterest = instance.getFeatures()[featureIndex];
        DiscretizerRelation relation = featureOfInterest.getDiscretizer().unApply(instance.getValue(featureIndex));
        AnchorPredicate predicate;
        switch (relation.getFeatureType()) {
            case METRIC:
                predicate = new AnchorPredicate(
                        featureOfInterest.getName(),
                        relation.getDiscretizedValue(),
                        candidate.getAddedPrecision(),
                        candidate.getAddedCoverage(),
                        relation.getConditionMin(),
                        relation.getConditionMax());
                break;
            case CATEGORICAL:
                predicate = new AnchorPredicate(
                        featureOfInterest.getName(),
                        relation.getDiscretizedValue(),
                        candidate.getAddedPrecision(),
                        candidate.getAddedCoverage(),
                        relation.getCategoricalValue()
                );
                break;
            case UNDEFINED:
            default:
                throw new IllegalArgumentException("relation with undefined feature type not handled: " + relation.toString());
        }
        predicateMap.put(candidate.getAddedFeature(), predicate);

        if (candidate.hasParentCandidate()) {
            createAnchorPredicateMap(predicateMap, candidate.getParentCandidate(), instance);
        }
    }

    public static void addColumnsToAnchorBuilder(final AnchorTabular.Builder builder,
                                                 final Map<String, Integer> header,
                                                 final String targetColumnName,
                                                 final Collection<ColumnSummary<?>> columnSummaries,
                                                 final Set<String> ignoredColumns,
                                                 final int classCount) {

        List<Map.Entry<String, Integer>> headList = new ArrayList<>(header.size());
        headList.addAll(header.entrySet());
        headList.sort(Comparator.comparingInt(Map.Entry::getValue));

        headList.forEach((entry) -> {
            String columnLabel = entry.getKey();
            ColumnSummary<?> columnSummary = findColumn(columnSummaries, columnLabel);
            GenericColumn column;
            if (ignoredColumns.contains(columnLabel)) {
                // ignore
                column = new IgnoredColumn(columnLabel, entry.getValue());
            } else if (H2oUtil.isColumnTypeInt(columnSummary.getColumn_type())) {
                column = new IntegerColumn(
                        columnLabel, entry.getValue(),
                        Arrays.asList(
                                new ReplaceNullTransformer(NoValueHandler.getNumberNa()),
                                new StringToIntTransformer()),
                        new PercentileMedianDiscretizer(classCount));
            } else if (H2oUtil.isColumnTypeReal(columnSummary.getColumn_type())) {
                column = new IntegerColumn(
                        columnLabel, entry.getValue(),
                        Arrays.asList(
                                new ReplaceNullTransformer(NoValueHandler.getNumberNa()),
                                new StringToDoubleTransformer()),
                        new PercentileMedianDiscretizer(classCount));
            } else if (H2oUtil.isColumnTypeEnum(columnSummary.getColumn_type()) ||
                    H2oUtil.isColumnTypeString(columnSummary.getColumn_type())) {
                column = new StringColumn(columnLabel, entry.getValue(), null, new UniqueValueDiscretizer());
            } else {
                throw new IllegalArgumentException("Column type " + columnSummary.getColumn_type() + " not handled");
            }

            if (columnLabel.equals(targetColumnName)) {
                builder.addTargetColumn(column);
            } else if (ignoredColumns.contains(columnLabel)) {
                builder.addIgnoredColumn(columnLabel);
            } else {
                builder.addColumn(column);
            }
        });
    }

    public static ColumnSummary<?> findColumn(final Collection<ColumnSummary<?>> columns, final String columnName) {
        return columns.stream().filter((column) -> column.getLabel().equals(columnName)).findFirst().orElseThrow(
                () -> new IllegalStateException("Column with name " + columnName + " not found"));
    }

}
