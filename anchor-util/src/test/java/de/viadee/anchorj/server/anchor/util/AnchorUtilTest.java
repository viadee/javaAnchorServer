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
import de.viadee.anchorj.server.model.Anchor;
import de.viadee.anchorj.server.model.AnchorPredicate;
import de.viadee.anchorj.server.model.CategoricalColumnSummary;
import de.viadee.anchorj.server.model.ColumnSummary;
import de.viadee.anchorj.server.model.ContinuousColumnSummary;
import de.viadee.xai.anchor.adapter.model.h2o.H2oTabularNominalMojoClassifier;
import de.viadee.xai.anchor.adapter.tabular.AnchorTabular;
import de.viadee.xai.anchor.adapter.tabular.TabularInstance;
import de.viadee.xai.anchor.adapter.tabular.column.GenericColumn;
import de.viadee.xai.anchor.adapter.tabular.column.IntegerColumn;
import de.viadee.xai.anchor.adapter.tabular.discretizer.UniqueValueDiscretizer;
import de.viadee.xai.anchor.algorithm.AnchorCandidate;
import de.viadee.xai.anchor.algorithm.AnchorResult;
import hex.genmodel.easy.EasyPredictModelWrapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 */
class AnchorUtilTest {

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
        AnchorTabular.Builder builder = new AnchorTabular.Builder();

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
        bColumn.setColumn_type("int");
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
        eColumn.setColumn_type("int");
        columns.add(eColumn);

        CategoricalColumnSummary<String> fColumn = new CategoricalColumnSummary<>();
        fColumn.setLabel("F");
        fColumn.setColumn_type("enum");
        columns.add(fColumn);


        Set<String> ignoredColumns = Collections.singleton("B");
        int classCount = 5;

        AnchorUtil.addColumnsToAnchorBuilder(builder, header, targetColumnName, columns, ignoredColumns, classCount);
        AnchorTabular tabular = builder.build(Collections.singleton(new String[]{"A-Value", "1", "C-String", "D-UUID", "4", "F-Enum"}));
        GenericColumn cd = tabular.getTargetColumn();
        assertEquals("A", cd.getName());
        assertEquals(targetColumnName, tabular.getTargetColumn().getName());
        assertTrue(cd.isDoUse());

        cd = tabular.getColumns().get(0);
        assertEquals("C", cd.getName());
        assertTrue(cd.isDoUse());

        cd = tabular.getColumns().get(1);
        assertEquals("D", cd.getName());
        assertTrue(cd.isDoUse());

        cd = tabular.getColumns().get(2);
        assertEquals("E", cd.getName());
        assertTrue(cd.isDoUse());
        assertEquals(4, cd.getDiscretizer().apply(4).intValue());

        cd = tabular.getColumns().get(3);
        assertEquals("F", cd.getName());
        assertTrue(cd.isDoUse());
        assertEquals(0, cd.getDiscretizer().apply("F-Enum").intValue());
    }

    @Test
    void testCalculateCoveragePerPredicate() {
        GenericColumn[] columns = new GenericColumn[3];
        columns[0] = new IntegerColumn("A");
        columns[1] = new IntegerColumn("B");
        columns[2] = new IntegerColumn("C");

        TabularInstance[] instances = new TabularInstance[5];
        instances[0] = new TabularInstance(columns, null, new String[] { "0", "1", "3" }, new Integer[] { 0, 1, 3 }, "", 0);
        instances[1] = new TabularInstance(columns, null, new String[] { "0", "2", "2" }, new Integer[] { 0, 2, 2 }, "", 0);
        instances[2] = new TabularInstance(columns, null, new String[] { "0", "1", "2" }, new Integer[] { 0, 1, 2 }, "", 0);
        instances[3] = new TabularInstance(columns, null, new String[] { "0", "2", "2" }, new Integer[] { 0, 2, 2 }, "", 0);
        instances[4] = new TabularInstance(columns, null, new String[] { "0", "1", "2" }, new Integer[] { 0, 1, 2 }, "", 0);

        ArrayList<Anchor> anchors = new ArrayList<>();
        Anchor a = new Anchor();
        Map<Integer, AnchorPredicate> predicates = new HashMap<>();
        predicates.put(0, new AnchorPredicate("C", 3, 0, 0, "3"));
        predicates.put(1, new AnchorPredicate("B", 1, 0, 0, 0, 2));
        a.setPredicates(predicates);
        anchors.add(a);

        AnchorUtil.calculateCoveragePerPredicate(instances, anchors);
        assertEquals(0.2, anchors.get(0).getPredicates().get(0).getExactCoverage().doubleValue());
    }

    @Test
    void testTransformAnchor() {
        String[] originalInstance = new String[2];
        originalInstance[0] = "100";
        originalInstance[1] = "2";
        String[] originalInstance2 = new String[2];
        originalInstance2[0] = "100";
        originalInstance2[1] = "2";

        GenericColumn[] columns = new GenericColumn[2];
        columns[0] = new IntegerColumn("A", null, null, new UniqueValueDiscretizer());
        columns[1] = new IntegerColumn("B", null, null, new UniqueValueDiscretizer());

        H2oTabularNominalMojoClassifier classifier = mock(H2oTabularNominalMojoClassifier.class);
        EasyPredictModelWrapper modelWrapper = mock(EasyPredictModelWrapper.class);
        when(classifier.getModelWrapper()).thenReturn(modelWrapper);
        when(modelWrapper.getResponseDomainValues()).thenReturn(new String[] { "0", "1" });

        AnchorTabular.Builder preprocessor = new AnchorTabular.Builder();
        AnchorTabular tabular = preprocessor.addColumn(columns[0]).addTargetColumn(columns[1]).build(Arrays.asList(originalInstance, originalInstance2));

        TabularInstance explainedInstance = tabular.getTabularInstances()[0];
        AnchorResultWithExactCoverage result = mock(AnchorResultWithExactCoverage.class);
        when(result.getInstance()).thenReturn(explainedInstance);
        when(result.getLabel()).thenReturn(1);
        when(result.getTimeSpent()).thenReturn(12.3);
        when(result.getTimeSpentSampling()).thenReturn(4.9);
        when(result.getPrecision()).thenReturn(0.89);
        when(result.getOrderedFeatures()).thenReturn(Collections.singletonList(0));
        when(result.getCoverage()).thenReturn(0.12);


        //noinspection unchecked
        Anchor transformed = AnchorUtil.transformAnchor("modelId", "frameId", 1, tabular, classifier, result);
        assertEquals("2", transformed.getLabel_of_case());
        assertEquals(0.12, transformed.getCoverage());
        assertEquals(0.89, transformed.getPrecision());
        // TODO complete test
    }

    @Test
    void testIsInstanceInAnchor() {
        GenericColumn[] columns = new GenericColumn[3];
        columns[0] = new IntegerColumn("A");
        columns[1] = new IntegerColumn("B");
        columns[2] = new IntegerColumn("C");

        TabularInstance[] instances = new TabularInstance[5];
        instances[0] = new TabularInstance(columns, null, null, new Integer[] { 0, 1, 3 }, null, null);
        instances[1] = new TabularInstance(columns, null, null, new Integer[] { 0, 2, 2 }, null, null);
        instances[2] = new TabularInstance(columns, null, null, new Integer[] { 0, 1, 2 }, null, null);
        instances[3] = new TabularInstance(columns, null, null, new Integer[] { 0, 2, 2 }, null, null);
        instances[4] = new TabularInstance(columns, null, null, new Integer[] { 0, 2, 1 }, null, null);

        AnchorCandidate candidate = new AnchorCandidate(Arrays.asList(0, 1));
        candidate.setCoverage(0);

        TabularInstance instanceToTest = new TabularInstance(columns, null, null, new Integer[] { 0, 1, 3 }, null, null);
        AnchorResult<TabularInstance> result = new AnchorResultWithExactCoverage(candidate, instanceToTest, 0, true, 0, 0);

        assertTrue(AnchorUtil.isInstanceInAnchor(instances[0], result.getInstance(), result.getOrderedFeatures()));
        assertFalse(AnchorUtil.isInstanceInAnchor(instances[1], result.getInstance(), result.getOrderedFeatures()));

        Set<TabularInstance> covered = AnchorUtil.findCoveredInstances(instances, Collections.singletonList(result));
        assertEquals(2, covered.size());
    }

}
