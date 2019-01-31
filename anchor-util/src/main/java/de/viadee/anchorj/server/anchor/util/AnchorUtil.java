package de.viadee.anchorj.server.anchor.util;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.viadee.anchorj.server.h2o.util.H2oColumnType;
import de.viadee.anchorj.server.model.Anchor;
import de.viadee.anchorj.server.model.AnchorPredicate;
import de.viadee.anchorj.server.model.ColumnSummary;
import de.viadee.xai.anchor.adapter.model.h2o.H2oTabularNominalMojoClassifier;
import de.viadee.xai.anchor.adapter.tabular.AnchorTabular;
import de.viadee.xai.anchor.adapter.tabular.TabularInstance;
import de.viadee.xai.anchor.adapter.tabular.column.DoubleColumn;
import de.viadee.xai.anchor.adapter.tabular.column.GenericColumn;
import de.viadee.xai.anchor.adapter.tabular.column.IgnoredColumn;
import de.viadee.xai.anchor.adapter.tabular.column.IntegerColumn;
import de.viadee.xai.anchor.adapter.tabular.column.StringColumn;
import de.viadee.xai.anchor.adapter.tabular.discretizer.DiscretizerRelation;
import de.viadee.xai.anchor.adapter.tabular.discretizer.PercentileMedianDiscretizer;
import de.viadee.xai.anchor.adapter.tabular.discretizer.UniqueValueDiscretizer;
import de.viadee.xai.anchor.adapter.tabular.transformations.ReplaceNullTransformer;
import de.viadee.xai.anchor.adapter.tabular.transformations.StringToDoubleTransformer;
import de.viadee.xai.anchor.adapter.tabular.transformations.StringToIntTransformer;
import de.viadee.xai.anchor.algorithm.AnchorCandidate;
import de.viadee.xai.anchor.algorithm.AnchorResult;
import de.viadee.xai.anchor.algorithm.DataInstance;

/**
 *
 */
public class AnchorUtil {

    private AnchorUtil() {
    }

    public static <T extends TabularInstance> void calculateCoveragePerPredicate(final T[] instances,
                                                                                 final Collection<Anchor> explanations) {
        T instance = instances[0];
        GenericColumn[] columns = instance.getFeatures();
        for (Anchor expl : explanations) {
            for (AnchorPredicate predicate : expl.getPredicates().values()) {
                Integer featureIndex = null;
                for (int i = 0; i < columns.length; i++) {
                    if (predicate.getFeatureName().equals(columns[i].getName())) {
                        featureIndex = i;
                    }
                }
                if (featureIndex == null) {
                    throw new IllegalArgumentException("No feature found with name " + predicate.getFeatureName());
                }

                AnchorCandidate fakeCandidate = new AnchorCandidate(Collections.singleton(featureIndex));
                fakeCandidate.setCoverage(0);
                TabularInstance predicateInstance = new TabularInstance(instance);
                predicateInstance.getInstance()[featureIndex] = predicate.getDiscretizedValue();
                AnchorResultWithExactCoverage anchorResult = new AnchorResultWithExactCoverage(
                        fakeCandidate,
                        predicateInstance,
                        0,
                        true,
                        0,
                        0
                );
                int foundInstances = findCoveredInstances(instances, Collections.singletonList(anchorResult)).size();
                predicate.setExactCoverage(foundInstances / (double) instances.length);
            }
        }

    }

    public static <T extends TabularInstance, E extends AnchorResult<T>> Set<T> findCoveredInstances(final T[] instances,
                                                                                                     final List<E> anchorResults) {
        Set<T> coveredInstances = new HashSet<>();
        for (T item : instances) {
            for (E result : anchorResults) {
                if (isInstanceInAnchor(item, result.getInstance(), result.getOrderedFeatures())) {
                    coveredInstances.add(item);
                }
            }
        }

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

        final int affectedRows = (int) Math.round(dataSetSize * anchorResult.getExactCoverage());
        convertedAnchor.setAffected_rows(affectedRows);

        String prediction = classificationFunction.getModelWrapper().getResponseDomainValues()[anchorResult.getLabel()];
        convertedAnchor.setPrediction(prediction);

        final Map<Integer, AnchorPredicate> anchorPredicateMap = new HashMap<>();
        createAnchorPredicateMap(anchorPredicateMap, anchorResult, anchorResult.getInstance());
        convertedAnchor.setPredicates(anchorPredicateMap);
        return convertedAnchor;
    }

    public static void createAnchorPredicateMap(Map<Integer, AnchorPredicate> predicateMap, AnchorCandidate candidate,
                                                TabularInstance instance) {
        if (candidate == null) {
            return;
        }

        int featureIndex = candidate.getAddedFeature();
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
        predicateMap.put(predicateMap.size(), predicate);

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

        for (Map.Entry<String, Integer> entry : headList) {
            String columnLabel = entry.getKey();
            ColumnSummary<?> columnSummary = findColumn(columnSummaries, columnLabel);
            GenericColumn column;
            if (ignoredColumns.contains(columnLabel)) {
                column = new IgnoredColumn(columnLabel);
            } else if (H2oColumnType.isColumnTypeInt(columnSummary.getColumn_type())) {
                column = new IntegerColumn(
                        columnLabel, null,
                        Arrays.asList(
                                new ReplaceNullTransformer(NoValueHandler.getNumberNa()),
                                new StringToIntTransformer()),
                        new PercentileMedianDiscretizer(classCount, NoValueHandler.getNumberNa()));
            } else if (H2oColumnType.isColumnTypeReal(columnSummary.getColumn_type())) {
                column = new DoubleColumn(
                        columnLabel, null,
                        Arrays.asList(
                                new ReplaceNullTransformer((double) NoValueHandler.getNumberNa()),
                                new StringToDoubleTransformer()),
                        new PercentileMedianDiscretizer(classCount, (double) NoValueHandler.getNumberNa()));
            } else if (H2oColumnType.isColumnTypeEnum(columnSummary.getColumn_type()) ||
                    H2oColumnType.isColumnTypeString(columnSummary.getColumn_type())) {
                column = new StringColumn(columnLabel, null, null, new UniqueValueDiscretizer());
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
        }
    }

    public static ColumnSummary<?> findColumn(final Collection<ColumnSummary<?>> columns, final String columnName) {
        return columns.stream().filter((column) -> column.getLabel().equals(columnName)).findFirst().orElseThrow(
                () -> new IllegalStateException("Column with name " + columnName + " not found"));
    }

}
