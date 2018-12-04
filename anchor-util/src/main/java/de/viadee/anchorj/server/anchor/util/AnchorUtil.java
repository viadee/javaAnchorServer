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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.viadee.anchorj.AnchorCandidate;
import de.viadee.anchorj.AnchorResult;
import de.viadee.anchorj.DataInstance;
import de.viadee.anchorj.server.h2o.util.H2oUtil;
import de.viadee.anchorj.server.model.Anchor;
import de.viadee.anchorj.server.model.AnchorPredicate;
import de.viadee.anchorj.server.model.AnchorPredicateEnum;
import de.viadee.anchorj.server.model.AnchorPredicateMetric;
import de.viadee.anchorj.server.model.ColumnSummary;
import de.viadee.anchorj.server.model.ContinuousColumnSummary;
import de.viadee.anchorj.tabular.AnchorTabular;
import de.viadee.anchorj.tabular.CategoricalValueMapping;
import de.viadee.anchorj.tabular.ColumnDescription;
import de.viadee.anchorj.tabular.FeatureValueMapping;
import de.viadee.anchorj.tabular.MetricValueMapping;
import de.viadee.anchorj.tabular.NativeValueMapping;
import de.viadee.anchorj.tabular.TabularFeature;
import de.viadee.anchorj.tabular.TabularInstance;
import hex.genmodel.easy.prediction.BinomialModelPrediction;
import hex.genmodel.easy.prediction.MultinomialModelPrediction;

/**
 *
 */
public class AnchorUtil {

    private static final Logger LOG = LoggerFactory.getLogger(AnchorUtil.class);

    private AnchorUtil() {
    }

    public static double computeExactCoverage(List<TabularInstance> instances, AnchorPredicate rule) {
        long count = instances.stream().filter((instance) -> {
            if (rule instanceof AnchorPredicateEnum) {
                return instance.getOriginalValue(rule.getFeatureName()).equals(((AnchorPredicateEnum) rule).getCategory());
            } else if (rule instanceof AnchorPredicateMetric) {
                double instanceValue = Double.valueOf(instance.getOriginalValue(rule.getFeatureName()).toString());
                return instanceValue >= ((AnchorPredicateMetric) rule).getConditionMin()
                        && instanceValue < ((AnchorPredicateMetric) rule).getConditionMax();
            } else {
                throw new IllegalArgumentException("Rule of class " + rule.getClass().getSimpleName() + " not handled");
            }
        }).count();

        return (double) count / instances.size();
    }

    public static <T extends DataInstance<?>> Set<T> computeGlobalCoverage(final List<T> instances,
                                                                           final List<AnchorResult<T>> anchorResults) {
        return AnchorUtil.runParallel(() -> {
            final Set<T> coveredInstances = new HashSet<>();
            instances.parallelStream().forEach(item -> {
                anchorResults.forEach((result) -> {
                    result.getOrderedFeatures().forEach((featureIndex) -> {
                        if (item.getValue(featureIndex).equals(result.getInstance().getValue(featureIndex))) {
                            coveredInstances.add(item);
                        }
                    });
                });
            });

            return coveredInstances;
        });
    }

    public static <T> T runParallel(Callable<T> call) {
        ForkJoinPool fjp = new ForkJoinPool(10, ForkJoinPool.defaultForkJoinWorkerThreadFactory, (t, e) ->
                LOG.error("uncaught exception of thread: " + t.getName() + " and error: " + e.getMessage(), e), false);
        try {
            return fjp.submit(call).get();
        } catch (InterruptedException | ExecutionException e) {
            LOG.error("interrupted filter: " + e.getMessage(), e);
        } finally {
            fjp.shutdown();
        }

        return null;
    }

    /**
     * Iterates through all nominal columns and replaces the empty strings with the value of @{@link NoValueHandler#getNumberNa()}
     *
     * @param header             the list of header and their column index
     * @param dataSet            the data set
     * @param columnDescriptions list of the columns
     */
    public static void handleNa(Map<String, Integer> header, Collection<String[]> dataSet, List<ColumnDescription> columnDescriptions) {
        final List<Integer> nominalColumnsIndexes = columnDescriptions.stream()
                .filter((predicate) -> predicate.getColumnType() == TabularFeature.ColumnType.NOMINAL)
                .mapToInt((description) -> header.get(description.getName()))
                .boxed().collect(Collectors.toList());

        AnchorUtil.runParallel(() -> {
                    dataSet.forEach((dataEntry) -> {
                        nominalColumnsIndexes.forEach((nominalColumnIndex) -> {
                            if (dataEntry[nominalColumnIndex].isEmpty()) {
                                dataEntry[nominalColumnIndex] = String.valueOf(NoValueHandler.getNumberNa());
                            }
                        });
                    });
                    return null;
                }
        );
    }

    public static AnchorCandidate findCandidate(AnchorCandidate candidate, Integer feature) {
        if (candidate.getAddedFeature().equals(feature)) {
            return candidate;
        } else if (candidate.getParentCandidate() != null) {
            return findCandidate(candidate.getParentCandidate(), feature);
        } else {
            return null;
        }
    }

    public static Anchor transformAnchor(String modelId, String frameId, int dataSetSize,
                                         AnchorTabular.TabularPreprocessorBuilder anchorBuilder, AnchorTabular anchor,
                                         H2oTabularMojoClassifier classificationFunction,
                                         AnchorResult<TabularInstance> anchorResult) {
        Anchor convertedAnchor = new Anchor();
        convertedAnchor.setCoverage(anchorResult.getCoverage());
        convertedAnchor.setPrecision(anchorResult.getPrecision());
        convertedAnchor.setCreated_at(LocalDateTime.now());
        convertedAnchor.setFeatures(anchorResult.getOrderedFeatures());
        convertedAnchor.setFrame_id(frameId);
        convertedAnchor.setModel_id(modelId);

        TabularInstance cleanedInstance = anchorResult.getInstance();
        Object labelOfCase = cleanedInstance.getLabel();
        convertedAnchor.setLabel_of_case(labelOfCase);

        Map<String, Serializable> cleanedInstanceMap = new HashMap<>(cleanedInstance.getFeatureCount());
        for (int i = 0; i < cleanedInstance.getFeatureCount(); i++) {
            cleanedInstanceMap.put(anchor.getFeatures().get(i).getName(), cleanedInstance.getOriginalValue(i));
        }
        convertedAnchor.setInstance(cleanedInstanceMap);

        final int affectedRows = (int) Math.round(dataSetSize * convertedAnchor.getCoverage());
        convertedAnchor.setAffected_rows(affectedRows);

        String prediction = classificationFunction.getModelWrapper().getResponseDomainValues()[anchorResult.getLabel()];
        convertedAnchor.setPrediction(prediction);

        final Map<Integer, AnchorPredicateEnum> enumAnchors = new HashMap<>();
        final Map<Integer, AnchorPredicateMetric> metricAnchors = new HashMap<>();
        for (Map.Entry<Integer, FeatureValueMapping> entry : anchor.getVisualizer().getAnchor(anchorResult).entrySet()) {
            final TabularFeature feature = entry.getValue().getFeature();

            final int featureIndex = anchorResult.getInstance().getFeatureArrayIndex(feature.getName());
            final AnchorCandidate candidate = AnchorUtil.findCandidate(anchorResult, featureIndex);
            double addedCoverage = 0;
            double addedPrecision = 0;
            if (candidate != null) {
                addedCoverage = candidate.getAddedCoverage();
                addedPrecision = candidate.getAddedPrecision();
            } else {
                LOG.error("No AnchorCandidate for feature with index " + featureIndex + " and name " + feature.getName() + " found");
            }

            final FeatureValueMapping featureValueMapping = entry.getValue();
            if (featureValueMapping instanceof CategoricalValueMapping) {
                String value = featureValueMapping.getValue().toString();
                enumAnchors.put(entry.getKey(), new AnchorPredicateEnum(feature.getName(), value, addedPrecision, addedCoverage));
            } else if (featureValueMapping instanceof NativeValueMapping) {
                enumAnchors.put(entry.getKey(), new AnchorPredicateEnum(feature.getName(),
                        featureValueMapping.getValue().toString(), addedPrecision, addedCoverage));
            } else if (featureValueMapping instanceof MetricValueMapping) {
                MetricValueMapping metric = (MetricValueMapping) featureValueMapping;
                metricAnchors.put(entry.getKey(), new AnchorPredicateMetric(feature.getName(),
                        metric.getMinValue(), metric.getMaxValue(), addedPrecision, addedCoverage));
            } else {
                throw new IllegalArgumentException("feature value mapping of type " +
                        featureValueMapping.getClass().getSimpleName() + " not handled");
            }
        }
        convertedAnchor.setEnumPredicate(enumAnchors);
        convertedAnchor.setMetricPredicate(metricAnchors);
        return convertedAnchor;
    }

    public static void addColumnsToAnchorBuilder(final AnchorTabular.TabularPreprocessorBuilder builder,
                                                 final Map<String, Integer> header,
                                                 final String targetColumnName,
                                                 final Collection<ColumnSummary<?>> columnSummary,
                                                 final Set<String> ignoredColumns,
                                                 final int classCount) {

        List<Map.Entry<String, Integer>> headList = new ArrayList<>(header.size());
        headList.addAll(header.entrySet());
        headList.sort(Comparator.comparingInt(Map.Entry::getValue));

        headList.forEach((entry) -> {
            String columnLabel = entry.getKey();
            ColumnSummary<?> column = findColumn(columnSummary, columnLabel);
            if (columnLabel.equals(targetColumnName)) {
                builder.addTargetColumn(columnLabel);
            } else if (ignoredColumns.contains(columnLabel)) {
                builder.addIgnoredColumn(columnLabel);
            } else if (H2oUtil.isEnumColumn(column.getColumn_type())) {
                builder.addCategoricalColumn(columnLabel);
            } else if (H2oUtil.isStringColumn(column.getColumn_type())) {
                builder.addObjectColumn(columnLabel);
            } else {
                double min = ((ContinuousColumnSummary) column).getColumn_min();
                double max = ((ContinuousColumnSummary) column).getColumn_max();
                Function<Number[], Integer[]> discretizer = new PercentileRangeDiscretizer(classCount, min, max);
                builder.addNominalColumn(columnLabel, discretizer);
            }
        });
    }

    public static ColumnSummary<?> findColumn(final Collection<ColumnSummary<?>> columns, final String columnName) {
        return columns.stream().filter((column) -> column.getLabel().equals(columnName)).findFirst().orElseThrow(
                () -> new IllegalStateException("Column with name " + columnName + " not found"));
    }

    /**
     * Handles {@link BinomialModelPrediction} and {@link MultinomialModelPrediction}.
     *
     * @return a function to extract the labelIndex value of the predictions
     */
    public static H2oTabularMojoClassifier.SerializableFunction generateH2oPredictor() {
        return (prediction) -> {
            if (prediction instanceof BinomialModelPrediction) {
                return ((BinomialModelPrediction) prediction).labelIndex;
            } else if (prediction instanceof MultinomialModelPrediction) {
                return ((MultinomialModelPrediction) prediction).labelIndex;
            } else {
                throw new UnsupportedOperationException("Prediction of type: " + prediction.getClass().getSimpleName()
                        + "; not supported");
            }
        };
    }

    public static TabularInstance handleInstanceToExplain(TabularInstance instance,
                                                          AnchorTabular.TabularPreprocessorBuilder anchorBuilder,
                                                          AnchorTabular tabular) {
        @SuppressWarnings("SuspiciousToArrayCall")
        String[] instanceAsStringArray = Arrays.asList(instance.getInstance()).toArray(new String[0]);
        Collection<String[]> anchorInstance = new ArrayList<>(1);
        anchorInstance.add(instanceAsStringArray);
        // TODO from column description use transformation and discretizer
        AnchorUtil.handleNa(instance.getFeatureNamesMapping(), anchorInstance, anchorBuilder.getColumnDescriptions());

        TabularInstance transformedInstance = anchorBuilder.build(anchorInstance).getTabularInstances().get(0);
        tabular.getMappings().entrySet().stream().filter((entry) -> !entry.getKey().isTargetFeature()).forEach((entry) -> {
            int featureIndex = transformedInstance.getFeatureArrayIndex(entry.getKey().getName());
            Serializable value = transformedInstance.getOriginalValue(featureIndex);
            entry.getValue().entrySet().stream().anyMatch((valueMapping) -> {
                if (valueMapping.getValue() instanceof CategoricalValueMapping) {
                    if (value.equals(valueMapping.getValue().getValue())) {
                        transformedInstance.getInstance()[featureIndex] = (Serializable) ((CategoricalValueMapping) valueMapping.getValue()).getCategoricalValue();
                        return true;
                    }
                }
                return false;
            });
        });
        return transformedInstance;
    }

    public static void calculateCoveragePerPredicate(final List<TabularInstance> instances,
                                                     final Collection<Anchor> explanations) {

        final Map<String, Double> predicateCoverage = new HashMap<>();
        final Consumer<AnchorPredicate> calculateCoverage = predicate -> {
            final String featureName = predicate.getFeatureName();
            double exactCoverage;
            if (!predicateCoverage.containsKey(featureName)) {
                exactCoverage = computeExactCoverage(instances, predicate);
                predicateCoverage.put(featureName, exactCoverage);
            } else {
                exactCoverage = predicateCoverage.get(featureName);
            }
            predicate.setExactCoverage(exactCoverage);
        };
        explanations.forEach((expl) -> {
            expl.getEnumPredicate().values().forEach(calculateCoverage);
            expl.getMetricPredicate().values().forEach(calculateCoverage);
        });

    }

}
