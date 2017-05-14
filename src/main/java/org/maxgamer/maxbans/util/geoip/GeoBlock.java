package org.maxgamer.maxbans.util.geoip;

/**
 * @author netherfoam
 */
public class GeoBlock implements Comparable<GeoBlock> {
    private final GeoCountry country;
    private final int minimum;
    private final int maximum;

    public GeoBlock(GeoCountry country, int minimum, int maximum) {
        this.country = country;
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public GeoCountry getCountry() {
        return country;
    }

    public int getMinimum() {
        return minimum;
    }

    public int getMaximum() {
        return maximum;
    }

    @Override
    public int compareTo(GeoBlock that) {
        // We can assume that our bounds never overlap
        return this.minimum - that.minimum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GeoBlock geoBlock = (GeoBlock) o;

        return minimum == geoBlock.minimum;

    }

    @Override
    public int hashCode() {
        return minimum;
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
