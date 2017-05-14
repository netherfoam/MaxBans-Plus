package org.maxgamer.maxbans.util.geoip;

/**
 * @author netherfoam
 */
public class GeoCountry {
    private final int id;
    private final String continentName;
    private final String continentCode;
    private final String countryName;
    private final String countryCode;

    public GeoCountry(int id, String continentName, String continentCode, String countryName, String countryCode) {
        this.id = id;
        this.continentName = continentName;
        this.continentCode = continentCode;
        this.countryName = countryName;
        this.countryCode = countryCode;
    }

    public int getId() {
        return id;
    }

    public String getContinentName() {
        return continentName;
    }

    public String getContinentCode() {
        return continentCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GeoCountry that = (GeoCountry) o;

        if (id != that.id) return false;
        if (!continentName.equals(that.continentName)) return false;
        if (!continentCode.equals(that.continentCode)) return false;
        if (!countryName.equals(that.countryName)) return false;

        return countryCode.equals(that.countryCode);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + continentName.hashCode();
        result = 31 * result + continentCode.hashCode();
        result = 31 * result + countryName.hashCode();
        result = 31 * result + countryCode.hashCode();

        return result;
    }

    @Override
    public String toString() {
        return "GeoCountry{" +
                "id=" + id +
                ", continentName='" + continentName + '\'' +
                ", continentCode='" + continentCode + '\'' +
                ", countryName='" + countryName + '\'' +
                ", countryCode='" + countryCode + '\'' +
                '}';
    }
}
