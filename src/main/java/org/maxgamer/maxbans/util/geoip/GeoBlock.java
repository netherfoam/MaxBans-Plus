package org.maxgamer.maxbans.util.geoip;

import java.math.BigInteger;
import java.util.Objects;

/**
 * @author netherfoam
 */
public class GeoBlock implements Comparable<GeoBlock> {
    private final GeoCountry country;
    private final BigInteger minimum;
    private final BigInteger maximum;

    public GeoBlock(GeoCountry country, BigInteger minimum, BigInteger maximum) {
        this.country = country;
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public GeoCountry getCountry() {
        return country;
    }

    public BigInteger getMinimum() {
        return minimum;
    }

    public BigInteger getMaximum() {
        return maximum;
    }

    @Override
    public int compareTo(GeoBlock that) {
        // We can assume that our bounds never overlap
        return this.minimum.subtract(that.minimum).signum();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GeoBlock geoBlock = (GeoBlock) o;

        return Objects.equals(minimum, geoBlock.minimum);

    }

    @Override
    public int hashCode() {
        return minimum.hashCode();
    }

    @Override
    public String toString() {
        return "GeoBlock{" +
                "country=" + country +
                ", minimum=" + minimum +
                ", maximum=" + maximum +
                '}';
    }
}
