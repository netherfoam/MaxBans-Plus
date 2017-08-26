package org.maxgamer.maxbans.util.geoip;

import com.google.common.net.InetAddresses;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.math.BigInteger;
import java.net.InetAddress;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author netherfoam
 */
public class GeoParser {
    private static final CSVFormat FORMAT = CSVFormat.RFC4180;
    private static final String IPV4_FILE = "GeoLite2-Country-Blocks-IPv4.csv";
    private static final String IPV6_FILE = "GeoLite2-Country-Blocks-IPv6.csv";

    public static GeoTable standard(InputStream geoliteSrc, String localeCode) {
        try {
            ZipInputStream zipInput = new ZipInputStream(geoliteSrc);

            ZipEntry entry;

            String countryFile = "GeoLite2-Country-Locations-" + localeCode + ".csv";

            InputStream countrySrc = null;
            InputStream ipv4Src = null;
            InputStream ipv6Src = null;

            while ((entry = zipInput.getNextEntry()) != null) {
                if (entry.getName().endsWith(countryFile)) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    byte[] data = new byte[4096];

                    int n;
                    while ((n = zipInput.read(data)) > 0) {
                        out.write(data, 0, n);
                    }

                    countrySrc = new ByteArrayInputStream(out.toByteArray());
                } else if (entry.getName().endsWith(IPV4_FILE)) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    byte[] data = new byte[4096];

                    int n;
                    while ((n = zipInput.read(data)) > 0) {
                        out.write(data, 0, n);
                    }

                    ipv4Src = new ByteArrayInputStream(out.toByteArray());
                } else if(entry.getName().endsWith(IPV6_FILE)) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    byte[] data = new byte[4096];

                    int n;
                    while ((n = zipInput.read(data)) > 0) {
                        out.write(data, 0, n);
                    }

                    ipv6Src = new ByteArrayInputStream(out.toByteArray());
                }
            }
            zipInput.close();

            if (countrySrc == null) {
                throw new FileNotFoundException("Couldn't find " + countryFile + " inside GeoLite");
            }

            if (ipv4Src == null) {
                throw new FileNotFoundException("Couldn't find " + IPV4_FILE + " inside GeoLite");
            }

            if (ipv6Src == null) {
                throw new FileNotFoundException("Couldn't find " + IPV6_FILE + " inside GeoLite");
            }

            GeoTable table = new GeoParser().parse(countrySrc, ipv4Src, ipv6Src);

            countrySrc.close();
            ipv4Src.close();
            ipv6Src.close();

            return table;
        } catch (IOException e) {
            throw new IllegalStateException("Can't process GeoIP Database", e);
        }

    }

    public GeoTable parse(InputStream countrySrc, InputStream... blockSrc) throws IOException {
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

    private TreeSet<GeoBlock> blocks(Map<Integer, GeoCountry> countries, InputStream... sources) throws IOException {
        TreeSet<GeoBlock> set = new TreeSet<>();

        for(InputStream blocks : sources) {
            CSVParser parser = FORMAT.withHeader().parse(new InputStreamReader(blocks));

            for (CSVRecord record : parser) {
                GeoBlock block = block(countries, record);
                set.add(block);
            }
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
        BigInteger minimum = new BigInteger(addr.getAddress());

        int maxBits =  ((minimum.toString(2).length() + 7) / 8) * 8 - bits;
        BigInteger mask = BigInteger.ONE.shiftLeft(maxBits).subtract(BigInteger.ONE);
        BigInteger maximum = minimum.or(mask);

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
