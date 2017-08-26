package org.maxgamer.maxbans.util.geoip;

import com.google.common.net.InetAddresses;

import java.math.BigInteger;
import java.net.InetAddress;
import java.util.Map;
import java.util.TreeSet;

/**
 * @author netherfoam
 */
public class GeoTable {
    public static final GeoCountry ANONYMOUS = new GeoCountry(-1, "Proxy", "NA", "Anonymous", "NA");

    private Map<Integer, GeoCountry> countries;
    private TreeSet<GeoBlock> blocks;

    public GeoTable(Map<Integer, GeoCountry> countries, TreeSet<GeoBlock> blocks) {
        this.countries = countries;
        this.blocks = blocks;
    }

    public GeoBlock getBlock(BigInteger ip) {
        GeoBlock dummy = new GeoBlock(null, ip, ip);

        GeoBlock block = blocks.floor(dummy);

        // minimum > ip
        if(block.getMinimum().compareTo(ip) > 0) return null;

        // maximum < ip
        if(block.getMaximum().compareTo(ip) <= 0) return null;

        return block;
    }

    public GeoBlock getBlock(String ip) {
        InetAddress addr = InetAddresses.forString(ip);

        return getBlock(new BigInteger(addr.getAddress()));
    }
}
