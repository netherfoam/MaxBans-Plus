package org.maxgamer.maxbans.service;

import org.maxgamer.maxbans.util.geoip.GeoBlock;
import org.maxgamer.maxbans.util.geoip.GeoCountry;
import org.maxgamer.maxbans.util.geoip.GeoParser;
import org.maxgamer.maxbans.util.geoip.GeoTable;

import java.io.InputStream;

/**
 * @author netherfoam
 */
public class GeoIPService {
    private GeoTable table;

    public GeoIPService(InputStream geoliteSrc, String localeCode) {
        table = GeoParser.standard(geoliteSrc, localeCode);
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
