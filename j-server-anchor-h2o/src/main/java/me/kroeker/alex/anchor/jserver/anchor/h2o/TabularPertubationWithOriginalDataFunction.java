package me.kroeker.alex.anchor.jserver.anchor.h2o;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import de.viadee.anchorj.PerturbationFunction;
import de.viadee.anchorj.global.ReconfigurablePerturbationFunction;
import de.viadee.anchorj.tabular.TabularInstance;

public class TabularPertubationWithOriginalDataFunction implements ReconfigurablePerturbationFunction<TabularInstance> {

    private final TabularInstance instance;
    private final TabularInstance[] perturbationData;

    public TabularPertubationWithOriginalDataFunction(TabularInstance instance, TabularInstance[] perturbationData) {
        this.instance = instance;
        this.perturbationData = perturbationData;
    }

    @Override
    public PerturbationFunction.PerturbationResult<TabularInstance> perturb(Set<Integer> immutableFeaturesIdx,
                                                                            int nrPerturbations) {
        // Extend list until space is large enough
        List<TabularInstance> shuffledPerturbations = new ArrayList<>();
        while (shuffledPerturbations.size() < nrPerturbations)
            shuffledPerturbations.addAll(Arrays.asList(perturbationData));
        Collections.shuffle(shuffledPerturbations);

        List<TabularInstance> rawResult = new ArrayList<>();
        List<boolean[]> featuresChanged = new ArrayList<>();
        for (int i = 0; i < nrPerturbations; i++) {
            TabularInstance perturbedInstance = shuffledPerturbations.get(i).clone();
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
        return new TabularPertubationWithOriginalDataFunction(instance, perturbationData);
    }

}
