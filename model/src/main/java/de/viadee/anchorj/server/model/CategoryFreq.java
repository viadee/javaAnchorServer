package de.viadee.anchorj.server.model;

import java.util.Objects;

/**
 */
public class CategoryFreq {
    private String name;
    private double freq;

    public CategoryFreq() {
    }

    public CategoryFreq(String name, double freq) {
        this.name = name;
        this.freq = freq;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getFreq() {
        return freq;
    }

    public void setFreq(double freq) {
        this.freq = freq;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoryFreq that = (CategoryFreq) o;
        return Double.compare(that.freq, freq) == 0 &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, freq);
    }

    @Override
    public String toString() {
        return "CategoryFreq{" +
                "name='" + name + '\'' +
                ", freq=" + freq +
                '}';
    }
}
