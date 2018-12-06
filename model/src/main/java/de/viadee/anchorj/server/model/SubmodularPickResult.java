package de.viadee.anchorj.server.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;

public class SubmodularPickResult implements Serializable {
    private static final long serialVersionUID = 854711332042540731L;

    private final Collection<Anchor> anchors;
    private final double aggregatedCoverage;

    public SubmodularPickResult(Collection<Anchor> anchors, double aggregatedCoverage) {
        this.anchors = anchors;
        this.aggregatedCoverage = aggregatedCoverage;
    }

    public Collection<Anchor> getAnchors() {
        return anchors;
    }

    public double getAggregatedCoverage() {
        return aggregatedCoverage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubmodularPickResult that = (SubmodularPickResult) o;
        return Double.compare(that.aggregatedCoverage, aggregatedCoverage) == 0 &&
                Objects.equals(anchors, that.anchors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(anchors, aggregatedCoverage);
    }

    @Override
    public String toString() {
        return "SubmodularPickResult{" +
                "anchors=" + anchors +
                ", aggregatedCoverage=" + aggregatedCoverage +
                '}';
    }
}
