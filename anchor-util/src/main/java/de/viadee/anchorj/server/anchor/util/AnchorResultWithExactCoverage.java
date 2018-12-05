package de.viadee.anchorj.server.anchor.util;

import java.util.Objects;

import de.viadee.anchorj.AnchorCandidate;
import de.viadee.anchorj.AnchorResult;
import de.viadee.anchorj.tabular.TabularInstance;

public class AnchorResultWithExactCoverage extends AnchorResult<TabularInstance> {
    private double exactCoverage;

    /**
     * Constructs the instance
     *
     * @param candidate         the {@link AnchorCandidate}
     * @param instance          the instance described
     * @param label             the instance's label
     * @param isAnchor          if true, marks the result is an anchor and adheres to the set constraints
     * @param timeSpent         the total time spent constructing the result
     * @param timeSpentSampling the total time spent sampling and evaluating candidates
     */
    public AnchorResultWithExactCoverage(AnchorCandidate candidate, TabularInstance instance, int label, boolean isAnchor, double timeSpent,
                                         double timeSpentSampling) {
        super(candidate, instance, label, isAnchor, timeSpent, timeSpentSampling);
    }

    public AnchorResultWithExactCoverage(AnchorResult<TabularInstance> result) {
        super(result, result.getInstance(), result.getLabel(), result.isAnchor(), result.getTimeSpent(), result.getTimeSpentSampling());
    }

    public double getExactCoverage() {
        return exactCoverage;
    }

    public void setExactCoverage(double exactCoverage) {
        this.exactCoverage = exactCoverage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AnchorResultWithExactCoverage that = (AnchorResultWithExactCoverage) o;
        return Double.compare(that.exactCoverage, exactCoverage) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), exactCoverage);
    }
}
