package de.viadee.anchorj.server.anchor.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import de.viadee.anchorj.AnchorCandidate;
import de.viadee.anchorj.AnchorResult;
import de.viadee.anchorj.server.model.Anchor;
import de.viadee.anchorj.server.model.AnchorPredicateEnum;
import de.viadee.anchorj.server.model.AnchorPredicateMetric;
import de.viadee.anchorj.server.model.CategoricalColumnSummary;
import de.viadee.anchorj.server.model.ColumnSummary;
import de.viadee.anchorj.server.model.ContinuousColumnSummary;
import de.viadee.anchorj.tabular.AnchorTabular;
import de.viadee.anchorj.tabular.CategoricalValueMapping;
import de.viadee.anchorj.tabular.ColumnDescription;
import de.viadee.anchorj.tabular.FeatureValueMapping;
import de.viadee.anchorj.tabular.TabularFeature;
import de.viadee.anchorj.tabular.TabularInstance;
import de.viadee.anchorj.tabular.TabularInstanceVisualizer;
import hex.genmodel.easy.EasyPredictModelWrapper;
import hex.genmodel.easy.prediction.BinomialModelPrediction;
import hex.genmodel.easy.prediction.MultinomialModelPrediction;
import hex.genmodel.easy.prediction.RegressionModelPrediction;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 *
 */
class AnchorUtilTest {

    @Test
    void testHandleNa() {
        Map<String, Integer> header = new HashMap<>(3);
        header.put("A", 0);
        header.put("B", 1);
        header.put("C", 2);

        List<ColumnDescription> columnDescriptions = Arrays.asList(
                new ColumnDescription(TabularFeature.ColumnType.NOMINAL, "A", true, false, null, null),
                new ColumnDescription(TabularFeature.ColumnType.NOMINAL, "B", true, false, null, null),
                new ColumnDescription(TabularFeature.ColumnType.NOMINAL, "C", true, false, null, null)
        );

        List<String[]> dataSet = new LinkedList<>();

        String[] data1 = new String[3];
        data1[0] = "1";
        data1[1] = "2";
        data1[2] = "3";
        dataSet.add(data1);

        String[] data2 = new String[3];
        data2[0] = "3";
        data2[1] = "1";
        data2[2] = "2";
        dataSet.add(data2);

        String[] data3 = new String[3];
        data3[0] = "3";
        data3[1] = "1";
        data3[2] = "";
        dataSet.add(data3);

        AnchorUtil.handleNa(header, dataSet, columnDescriptions);

        assertEquals(data1[0], dataSet.get(0)[0]);
        assertEquals(data1[1], dataSet.get(0)[1]);
        assertEquals(data1[2], dataSet.get(0)[2]);

        assertEquals(data2[0], dataSet.get(1)[0]);
        assertEquals(data2[1], dataSet.get(1)[1]);
        assertEquals(data2[2], dataSet.get(1)[2]);

        assertEquals(data3[0], dataSet.get(2)[0]);
        assertEquals(data3[1], dataSet.get(2)[1]);
        assertEquals(String.valueOf(NoValueHandler.getNumberNa()), dataSet.get(2)[2]);
    }

    @Test
    void testFindColumn() {
        final List<ColumnSummary<?>> columns = new LinkedList<>();
        CategoricalColumnSummary<String> aColumn = new CategoricalColumnSummary<>();
        aColumn.setLabel("A");
        columns.add(aColumn);
        ContinuousColumnSummary bColumn = new ContinuousColumnSummary();
        bColumn.setLabel("B");
        columns.add(bColumn);
        CategoricalColumnSummary<String> cColumn = new CategoricalColumnSummary<>();
        cColumn.setLabel("C");
        columns.add(cColumn);
        CategoricalColumnSummary<String> dColumn = new CategoricalColumnSummary<>();
        dColumn.setLabel("D");
        columns.add(dColumn);

        assertEquals("C", AnchorUtil.findColumn(columns, "C").getLabel());
    }

    @Test
    void testFindColumnNotSpecified() {
        final List<ColumnSummary<?>> columns = new LinkedList<>();
        CategoricalColumnSummary<String> aColumn = new CategoricalColumnSummary<>();
        aColumn.setLabel("A");
        columns.add(aColumn);

        assertThrows(IllegalStateException.class, () -> AnchorUtil.findColumn(columns, "B"));
    }

    @Test
    void testAddColumnsToAnchorBuilder() {
        AnchorTabular.TabularPreprocessorBuilder builder = new AnchorTabular.TabularPreprocessorBuilder();

        Map<String, Integer> header = new HashMap<>(3);
        header.put("A", 0);
        header.put("B", 1);
        header.put("C", 2);
        header.put("D", 3);
        header.put("E", 4);
        header.put("F", 5);

        String targetColumnName = "A";

        final List<ColumnSummary<?>> columns = new LinkedList<>();

        CategoricalColumnSummary<String> aColumn = new CategoricalColumnSummary<>();
        aColumn.setLabel("A");
        aColumn.setColumn_type("enum");
        columns.add(aColumn);

        ContinuousColumnSummary bColumn = new ContinuousColumnSummary();
        bColumn.setLabel("B");
        bColumn.setColumn_min(1);
        bColumn.setColumn_max(10);
        bColumn.setColumn_type("metric");
        columns.add(bColumn);

        CategoricalColumnSummary<String> cColumn = new CategoricalColumnSummary<>();
        cColumn.setLabel("C");
        cColumn.setColumn_type("string");
        columns.add(cColumn);

        CategoricalColumnSummary<String> dColumn = new CategoricalColumnSummary<>();
        dColumn.setLabel("D");
        dColumn.setColumn_type("uuid");
        columns.add(dColumn);

        ContinuousColumnSummary eColumn = new ContinuousColumnSummary();
        eColumn.setLabel("E");
        eColumn.setColumn_min(1);
        eColumn.setColumn_max(8);
        eColumn.setColumn_type("metric");
        columns.add(eColumn);

        CategoricalColumnSummary<String> fColumn = new CategoricalColumnSummary<>();
        fColumn.setLabel("F");
        fColumn.setColumn_type("enum");
        columns.add(fColumn);


        Set<String> ignoredColumns = Collections.singleton("B");
        int classCount = 5;

        AnchorUtil.addColumnsToAnchorBuilder(builder, header, targetColumnName, columns, ignoredColumns, classCount);
        ColumnDescription cd = builder.getColumnDescriptions().get(0);
        assertEquals("A", cd.getName());
        assertTrue(cd.isTargetFeature());
        assertTrue(cd.isDoUse());
        assertEquals(TabularFeature.ColumnType.CATEGORICAL, cd.getColumnType());

        cd = builder.getColumnDescriptions().get(1);
        assertEquals("B", cd.getName());
        assertFalse(cd.isTargetFeature());
        assertFalse(cd.isDoUse());
        assertNull(cd.getColumnType());

        cd = builder.getColumnDescriptions().get(2);
        assertEquals("C", cd.getName());
        assertFalse(cd.isTargetFeature());
        assertTrue(cd.isDoUse());
        assertEquals(TabularFeature.ColumnType.NATIVE, cd.getColumnType());

        cd = builder.getColumnDescriptions().get(3);
        assertEquals("D", cd.getName());
        assertFalse(cd.isTargetFeature());
        assertTrue(cd.isDoUse());
        assertEquals(TabularFeature.ColumnType.NATIVE, cd.getColumnType());

        cd = builder.getColumnDescriptions().get(4);
        assertEquals("E", cd.getName());
        assertFalse(cd.isTargetFeature());
        assertTrue(cd.isDoUse());
        assertEquals(TabularFeature.ColumnType.NOMINAL, cd.getColumnType());
        assertEquals(0, cd.getDiscretizer().apply(new Number[] { 1 })[0].intValue());
        assertEquals(1, cd.getDiscretizer().apply(new Number[] { 3 })[0].intValue());
    }

    @Test
    void testCalculateCoveragePerPredicate() {
        Map<String, Integer> header = new HashMap<>();
        header.put("A", 0);
        header.put("B", 1);
        header.put("C", 2);

        List<TabularInstance> instances = new LinkedList<>();
        instances.add(new TabularInstance(header, null, new String[0], new String[] { "0", "1", "3" }));
        instances.add(new TabularInstance(header, null, new String[0], new String[] { "0", "2", "2" }));
        instances.add(new TabularInstance(header, null, new String[0], new String[] { "0", "1", "2" }));
        instances.add(new TabularInstance(header, null, new String[0], new String[] { "0", "2", "2" }));
        instances.add(new TabularInstance(header, null, new String[0], new String[] { "0", "1", "2" }));

        ArrayList<Anchor> anchors = new ArrayList<>();
        Anchor a = new Anchor();
        Map<Integer, AnchorPredicateEnum> enumPredicates = new HashMap<>();
        Map<Integer, AnchorPredicateMetric> metricPredicates = new HashMap<>();
        enumPredicates.put(0, new AnchorPredicateEnum("C", "3", 0, 0));
        metricPredicates.put(0, new AnchorPredicateMetric("B", 0, 2, 0, 0));
        a.setEnumPredicate(enumPredicates);
        a.setMetricPredicate(metricPredicates);
        anchors.add(a);

        AnchorUtil.calculateCoveragePerPredicate(instances, anchors);
        assertEquals(0.2, anchors.get(0).getEnumPredicate().get(0).getExactCoverage());
    }

    @Test
    void testTransformAnchor() {
        Map<String, Integer> header = new HashMap<>();
        header.put("A", 0);
        header.put("B", 1);
        String[] originalInstance = new String[2];
        originalInstance[0] = "100";
        originalInstance[1] = "2";


        String[] originalInstance2 = new String[2];
        originalInstance2[0] = "100";
        originalInstance2[1] = "2";

        H2oTabularMojoClassifier classifier = mock(H2oTabularMojoClassifier.class);
        EasyPredictModelWrapper modelWrapper = mock(EasyPredictModelWrapper.class);
        when(classifier.getModelWrapper()).thenReturn(modelWrapper);
        when(modelWrapper.getResponseDomainValues()).thenReturn(new String[] { "0", "1" });

        AnchorTabular.TabularPreprocessorBuilder preprocessor = new AnchorTabular.TabularPreprocessorBuilder();
        AnchorTabular tabular = spy(preprocessor.addCategoricalColumn("A").addTargetColumn("B").build(Arrays.asList(originalInstance, originalInstance2)));
        TabularInstanceVisualizer visualizer = mock(TabularInstanceVisualizer.class);
        when(tabular.getVisualizer()).thenReturn(visualizer);

        Map<Integer, FeatureValueMapping> discretizedMapping = new HashMap<>();
        CategoricalValueMapping val1 = new CategoricalValueMapping(tabular.getFeatures().get(0), 1, "100");
        discretizedMapping.put(0, val1);
        when(visualizer.getAnchor(any())).thenReturn(discretizedMapping);

        TabularInstance explainedInstance = tabular.getTabularInstances().get(0);
        AnchorResultWithExactCoverage result = mock(AnchorResultWithExactCoverage.class);
        when(result.getInstance()).thenReturn(explainedInstance);
        when(result.getLabel()).thenReturn(1);
        when(result.getTimeSpent()).thenReturn(12.3);
        when(result.getTimeSpentSampling()).thenReturn(4.9);
        when(result.getCoverage()).thenReturn(0.12);
        when(result.getPrecision()).thenReturn(0.89);
        when(result.getOrderedFeatures()).thenReturn(Collections.singletonList(1));


        Anchor transformed = AnchorUtil.transformAnchor("modelId", "frameId", 1, tabular, classifier, result);
        assertEquals("2", transformed.getLabel_of_case());
        assertEquals(0.12, transformed.getCoverage());
        assertEquals(0.89, transformed.getPrecision());
        // TODO complete test
    }

    @Test
    void testGenerateH2oPredictor() {
        BinomialModelPrediction biPrediction = new BinomialModelPrediction();
        biPrediction.labelIndex = 1;
        assertEquals(1, AnchorUtil.generateH2oPredictor().apply(biPrediction).intValue());

        MultinomialModelPrediction multiPrediction = new MultinomialModelPrediction();
        multiPrediction.labelIndex = 1;
        assertEquals(1, AnchorUtil.generateH2oPredictor().apply(multiPrediction).intValue());

        RegressionModelPrediction regrPrediction = new RegressionModelPrediction();
        regrPrediction.value = 1.0;
        assertThrows(UnsupportedOperationException.class, () -> AnchorUtil.generateH2oPredictor().apply(regrPrediction));
    }

    @Test
    void testIsInstanceInAnchor() {
        Map<String, Integer> header = new HashMap<>();
        header.put("A", 0);
        header.put("B", 1);
        header.put("C", 2);

        List<TabularInstance> instances = new LinkedList<>();
        instances.add(new TabularInstance(header, null, new Integer[]{0, 1, 3}));
        instances.add(new TabularInstance(header, null, new Integer[]{0, 2, 2}));
        instances.add(new TabularInstance(header, null, new Integer[]{0, 1, 2}));
        instances.add(new TabularInstance(header, null, new Integer[]{0, 2, 2}));
        instances.add(new TabularInstance(header, null, new Integer[]{0, 2, 1}));

        AnchorCandidate candidate = new AnchorCandidate(Arrays.asList(0, 1));
        candidate.setCoverage(0);

        TabularInstance instanceToTest = new TabularInstance(header, null, new Integer[]{0, 1, 3});
        AnchorResult<TabularInstance> result = new AnchorResultWithExactCoverage(candidate, instanceToTest, 0, true, 0, 0);

        assertTrue(AnchorUtil.isInstanceInAnchor(instances.get(0), result));
        assertFalse(AnchorUtil.isInstanceInAnchor(instances.get(1), result));

        Set<TabularInstance> covered = AnchorUtil.findCoveredInstances(instances, Collections.singletonList(result));
        assertEquals(2, covered.size());
    }

}
