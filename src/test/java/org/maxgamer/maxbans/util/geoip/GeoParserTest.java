package org.maxgamer.maxbans.util.geoip;

import junit.framework.Assert;
import org.junit.Test;
import org.maxgamer.maxbans.test.IntegrationTest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author netherfoam
 */
public class GeoParserTest implements IntegrationTest {
    @Test
    public void testParser() throws URISyntaxException, IOException {
        InputStream geoLiteZip = getClass().getClassLoader().getResourceAsStream("GeoLite.zip");

        ZipInputStream zipInput = new ZipInputStream(geoLiteZip);

        ZipEntry entry;

        String countryFile = "GeoLite2-Country-Locations-en.csv";
        String ipv4File = "GeoLite2-Country-Blocks-IPv4.csv";

        InputStream countrySrc = null;
        InputStream ipv4Src = null;

        while((entry = zipInput.getNextEntry()) != null) {
            if(entry.getName().endsWith(countryFile)) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] data = new byte[4096];

                int n;
                while((n = zipInput.read(data)) > 0) {
                    out.write(data, 0, n);
                }

                countrySrc = new ByteArrayInputStream(out.toByteArray());
            }

            if(entry.getName().endsWith(ipv4File)) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] data = new byte[4096];

                int n;
                while((n = zipInput.read(data)) > 0) {
                    out.write(data, 0, n);
                }

                ipv4Src = new ByteArrayInputStream(out.toByteArray());
            }
        }

        zipInput.close();

        GeoParser parser = new GeoParser();
        GeoTable table = parser.parse(countrySrc, ipv4Src);

        if (countrySrc != null) {
            countrySrc.close();
        }

        if (ipv4Src != null) {
            ipv4Src.close();
        }

        GeoBlock localhost = table.getBlock("127.0.0.1");
        Assert.assertNull("Localhost isn't in a country", localhost);

        GeoBlock australian = table.getBlock("110.23.24.48");
        GeoCountry australia = australian.getCountry();
        Assert.assertNotNull("Australian", australian);
        Assert.assertEquals("Expect country code", "AU", australia.getCountryCode());
        Assert.assertEquals("Expect country name", "Australia", australia.getCountryName());
        Assert.assertEquals("Expect continent name", "Oceania", australia.getContinentName());
        Assert.assertEquals("Expect continent code", "OC", australia.getContinentCode());
    }
}
