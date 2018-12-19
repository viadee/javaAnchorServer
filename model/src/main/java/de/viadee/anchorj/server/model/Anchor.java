package de.viadee.anchorj.server.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 */
@SuppressWarnings({ "unused", "ClassWithTooManyMethods", "ClassWithTooManyFields" })
public class Anchor implements Serializable {
    private static final long serialVersionUID = 4391527342934385039L;

    private String model_id;
    private String frame_id;
    private Map<String, Serializable> instance;
    private Serializable label_of_case;
    private String prediction;
    private double precision;
    private double coverage;
    private Collection<Integer> features;
    private Map<Integer, AnchorPredicate> predicates;
    private Integer affected_rows;
    private LocalDateTime created_at;

    public String getModel_id() {
        return model_id;
    }

    public void setModel_id(String model_id) {
        this.model_id = model_id;
    }

    public String getFrame_id() {
        return frame_id;
    }

    public void setFrame_id(String frame_id) {
        this.frame_id = frame_id;
    }

    public Map<String, Serializable> getInstance() {
        return instance;
    }

    public void setInstance(Map<String, Serializable> instance) {
        this.instance = instance;
    }

    public Serializable getLabel_of_case() {
        return label_of_case;
    }

    public void setLabel_of_case(Serializable label_of_case) {
        this.label_of_case = label_of_case;
    }

    public String getPrediction() {
        return prediction;
    }

    public void setPrediction(String prediction) {
        this.prediction = prediction;
    }

    public double getPrecision() {
        return precision;
    }

    public void setPrecision(double precision) {
        this.precision = precision;
    }

    public double getCoverage() {
        return coverage;
    }

    public void setCoverage(double coverage) {
        this.coverage = coverage;
    }

    public Collection<Integer> getFeatures() {
        return features;
    }

    public void setFeatures(Collection<Integer> features) {
        this.features = features;
    }

    public Integer getAffected_rows() {
        return affected_rows;
    }

    public void setAffected_rows(Integer affected_rows) {
        this.affected_rows = affected_rows;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public Map<Integer, AnchorPredicate> getPredicates() {
        return predicates;
    }

    public void setPredicates(Map<Integer, AnchorPredicate> predicates) {
        this.predicates = predicates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Anchor anchor = (Anchor) o;
        return Double.compare(anchor.precision, precision) == 0 &&
                Double.compare(anchor.coverage, coverage) == 0 &&
                Objects.equals(model_id, anchor.model_id) &&
                Objects.equals(frame_id, anchor.frame_id) &&
                Objects.equals(instance, anchor.instance) &&
                Objects.equals(label_of_case, anchor.label_of_case) &&
                Objects.equals(prediction, anchor.prediction) &&
                Objects.equals(features, anchor.features) &&
                Objects.equals(predicates, anchor.predicates) &&
                Objects.equals(affected_rows, anchor.affected_rows) &&
                Objects.equals(created_at, anchor.created_at);
    }

    @Override
    public int hashCode() {
        return Objects.hash(model_id, frame_id, instance, label_of_case, prediction, precision, coverage, features, predicates, affected_rows, created_at);
    }

    @Override
    public String toString() {
        return "Anchor{" +
                "model_id='" + model_id + '\'' +
                ", frame_id='" + frame_id + '\'' +
                ", instance=" + instance +
                ", label_of_case=" + label_of_case +
                ", prediction='" + prediction + '\'' +
                ", precision=" + precision +
                ", coverage=" + coverage +
                ", features=" + features +
                ", predicates=" + predicates +
                ", affected_rows=" + affected_rows +
                ", created_at=" + created_at +
                '}';
    }
}
