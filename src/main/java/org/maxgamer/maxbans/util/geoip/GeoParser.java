package org.maxgamer.maxbans.util.geoip;

import com.google.common.net.InetAddresses;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * @author netherfoam
 */
public class GeoParser {
    private static final CSVFormat FORMAT = CSVFormat.RFC4180;

    public GeoTable parse(InputStream countrySrc, InputStream blockSrc) throws IOException {
        Map<Integer, GeoCountry> countries = countries(countrySrc);
        TreeSet<GeoBlock> blocks = blocks(countries, blockSrc);

        return new GeoTable(countries, blocks);
    }

    private Map<Integer, GeoCountry> countries(InputStream countries) throws IOException {
        CSVParser parser = FORMAT.withHeader().parse(new InputStreamReader(countries));
        Map<Integer, GeoCountry> map = new LinkedHashMap<>();

        for(CSVRecord record : parser) {
            GeoCountry country = country(record);
            map.put(country.getId(), country);
        }

        parser.close();

        return map;
    }

    private GeoCountry country(CSVRecord record) {
        int id = Integer.parseInt(record.get("geoname_id"));
        String continentName = record.get("continent_name");
        String continentCode = record.get("continent_code");
        String countryName = record.get("country_name");
        String countryCode = record.get("country_iso_code");

        return new GeoCountry(id, continentName, continentCode, countryName, countryCode);
    }

    private TreeSet<GeoBlock> blocks(Map<Integer, GeoCountry> countries, InputStream blocks) throws IOException {
        CSVParser parser = FORMAT.withHeader().parse(new InputStreamReader(blocks));
        TreeSet<GeoBlock> set = new TreeSet<>();

        for(CSVRecord record : parser) {
            GeoBlock block = block(countries, record);
            set.add(block);
        }

        return set;
    }

    private GeoBlock block(Map<Integer, GeoCountry> countries, CSVRecord record) throws IOException {
        String network = record.get("network");
        String[] parts = network.split("/");
        if(parts.length != 2) throw new IOException("Expect 2 network parts: ip/bits, got " + network);

        String ip = parts[0];
        int bits = Integer.parseInt(parts[1]);

        InetAddress addr = InetAddresses.forString(ip);
        int minimum = InetAddresses.coerceToInteger(addr);

        int maskedBits = 32 - bits;

        int mask = (1 << maskedBits) - 1;
        int maximum = minimum | mask;

        String countryName = record.get("registered_country_geoname_id");
        if(countryName.isEmpty()) {
            countryName = record.get("geoname_id");
        }

        GeoCountry country;
        if(!countryName.isEmpty()) {
            int countryId = Integer.parseInt(countryName);
            country = countries.get(countryId);
        } else {
            country = GeoTable.ANONYMOUS;
        }

        return new GeoBlock(country, minimum, maximum);
    }
}
