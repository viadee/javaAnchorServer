package de.viadee.anchorj.server.anchor.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import de.viadee.anchorj.PerturbationFunction;
import de.viadee.anchorj.global.ReconfigurablePerturbationFunction;
import de.viadee.anchorj.tabular.TabularInstance;

public class TabularWithOriginalDataPerturbationFunction implements ReconfigurablePerturbationFunction<TabularInstance> {
    private static final long serialVersionUID = -6713418284295608752L;

    private final TabularInstance instance;
    private final TabularInstance[] perturbationData;
    private List<TabularInstance> shuffledPerturbations;

    public TabularWithOriginalDataPerturbationFunction(TabularInstance instance, TabularInstance[] perturbationData) {
        this.instance = instance;
        this.perturbationData = perturbationData;
        this.shuffledPerturbations = Arrays.asList(perturbationData);
        Collections.shuffle(this.shuffledPerturbations);
    }

    @Override
    public PerturbationFunction.PerturbationResult<TabularInstance> perturb(Set<Integer> immutableFeaturesIdx,
                                                                            int nrPerturbations) {
        List<TabularInstance> rawResult = new ArrayList<>();
        List<boolean[]> featuresChanged = new ArrayList<>();
        for (int i = 0; i < nrPerturbations; i++) {
            TabularInstance perturbedInstance = shuffledPerturbations
                    .get(ThreadLocalRandom.current().nextInt(this.shuffledPerturbations.size())).clone();
            for (Integer featureId : immutableFeaturesIdx) {
                // Copy all fixed features
                perturbedInstance.getInstance()[featureId] = instance.getInstance()[featureId];
                perturbedInstance.getOriginalInstance()[featureId] = instance.getOriginalInstance()[featureId];
            }
            rawResult.add(perturbedInstance);
            boolean[] tempFeatureChanged = new boolean[perturbedInstance.getFeatureCount()];
            for (int j = 0; j < tempFeatureChanged.length; j++) {
                tempFeatureChanged[j] = !instance.getInstance()[j].equals(perturbedInstance.getInstance()[j]);
            }
            featuresChanged.add(tempFeatureChanged);
        }

        return new PerturbationResultImpl<>(rawResult.toArray(new TabularInstance[0]),
                featuresChanged.toArray(new boolean[0][]));
    }

    @Override
    public PerturbationFunction<TabularInstance> createForInstance(TabularInstance instance) {
        return new TabularWithOriginalDataPerturbationFunction(instance, this.perturbationData);
    }

}
