package org.maxgamer.maxbans.service;

import org.maxgamer.maxbans.util.geoip.GeoBlock;
import org.maxgamer.maxbans.util.geoip.GeoCountry;
import org.maxgamer.maxbans.util.geoip.GeoParser;
import org.maxgamer.maxbans.util.geoip.GeoTable;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author netherfoam
 */
public class GeoIPService {
    private GeoTable table;

    public GeoIPService(InputStream geoliteSrc) {
        try {
            ZipInputStream zipInput = new ZipInputStream(geoliteSrc);

            ZipEntry entry;

            String countryFile = "GeoLite2-Country-Locations-en.csv";
            String ipv4File = "GeoLite2-Country-Blocks-IPv4.csv";

            InputStream countrySrc = null;
            InputStream ipv4Src = null;

            while ((entry = zipInput.getNextEntry()) != null) {
                if (entry.getName().endsWith(countryFile)) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    byte[] data = new byte[4096];

                    int n;
                    while ((n = zipInput.read(data)) > 0) {
                        out.write(data, 0, n);
                    }

                    countrySrc = new ByteArrayInputStream(out.toByteArray());
                }

                if (entry.getName().endsWith(ipv4File)) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    byte[] data = new byte[4096];

                    int n;
                    while ((n = zipInput.read(data)) > 0) {
                        out.write(data, 0, n);
                    }

                    ipv4Src = new ByteArrayInputStream(out.toByteArray());
                }
            }
            zipInput.close();

            if (countrySrc == null) {
                throw new FileNotFoundException("Couldn't find " + countryFile + " inside GeoLite");
            }

            if (ipv4Src == null) {
                throw new FileNotFoundException("Couldn't find " + ipv4File + " inside GeoLite");
            }

            table = new GeoParser().parse(countrySrc, ipv4Src);

            countrySrc.close();
            ipv4Src.close();
        } catch (IOException e) {
            throw new IllegalStateException("Can't process GeoIP Database", e);
        }
    }

    public GeoCountry getCountry(String ipv4) {
        GeoBlock block = table.getBlock(ipv4);

        if(block == null) return null;

        return block.getCountry();
    }

    public GeoTable getTable() {
        return table;
    }
}
