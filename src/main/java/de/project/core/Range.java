package de.project.core;

import java.util.Objects;

/**
 * Class that represents a range between a given min and max value
 */
public class Range {
    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    private double min;
    private double max;

    /**
     * Checks if the value is inside the range
     * @param value The value
     * @return True, if the value is inside the range
     */
    public boolean contains(double value) {
        return min <= value && value <= max;
    }

    @Override
    public int hashCode() {
        return Objects.hash(min, max);
    }

    public Range(double min, double max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public String toString() {
        return "Range{" +
                "min=" + min +
                ", max=" + max +
                '}';
    }
}
