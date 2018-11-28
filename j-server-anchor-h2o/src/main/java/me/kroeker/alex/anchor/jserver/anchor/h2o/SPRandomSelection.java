package me.kroeker.alex.anchor.jserver.anchor.h2o;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class SPRandomSelection {

    private final Random randomGen;
    private final List<String[]> instances;

    public SPRandomSelection(Random randomGen, List<String[]> instances) {
        this.randomGen = randomGen;
        this.instances = instances;
    }

    public Set<String[]> getRandomSelection(int absoluteSize) {
        if (absoluteSize < 0) {
            throw new IllegalArgumentException("absolute size of " + absoluteSize + " is not applicable for " + this.instances.size() + " instances");
        }
        if (absoluteSize > instances.size()) {
            absoluteSize = instances.size();
        }

        return this.select(absoluteSize);
    }

    public Set<String[]> getRandomSelection(double percentage) {
        if (percentage > 1 || percentage < 0) {
            throw new IllegalArgumentException("percentage of " + percentage + " is not applicable");
        }

        int randomSize = (int) (instances.size() * percentage);
        return select(randomSize);
    }

    private Set<String[]> select(int randomSize) {
        Set<String[]> randomSelection = new HashSet<>();
        while (randomSelection.size() < randomSize) {
            randomSelection.add(this.instances.get(randomGen.nextInt(instances.size())));
        }

        return randomSelection;
    }
}
