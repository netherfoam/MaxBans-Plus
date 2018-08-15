package org.maxgamer.maxbans.util.geoip;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.maxgamer.maxbans.test.IntegrationTest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

/**
 * @author netherfoam
 */
public class GeoParserTest implements IntegrationTest {
    private GeoTable table;

    @Before
    public void init() throws IOException {
        InputStream geoLiteZip = getClass().getClassLoader().getResourceAsStream("GeoLite.zip");
        table = GeoParser.standard(geoLiteZip, "en");
    }

    @Test
    public void testLocalhost() throws URISyntaxException, IOException {
        GeoBlock localhost = table.getBlock("127.0.0.1");
        Assert.assertNull("Localhost isn't in a country", localhost);
    }

    @Test
    public void testIpv6() throws URISyntaxException, IOException {
        GeoBlock localhost = table.getBlock("::1");
        Assert.assertNull("Localhost isn't in a country", localhost);
    }

    @Test
    public void testIpv6Country() {
        GeoBlock taiwanese = table.getBlock("2001:288:106:8000::");
        Assert.assertNotNull("Expected a block", taiwanese);

        GeoCountry taiwan = taiwanese.getCountry();
        Assert.assertEquals("Expect country code", "TW", taiwan.getCountryCode());
        Assert.assertEquals("Expect country name", "Taiwan", taiwan.getCountryName());
        Assert.assertEquals("Expect continent name", "Asia", taiwan.getContinentName());
        Assert.assertEquals("Expect continent code", "AS", taiwan.getContinentCode());
    }

    @Test
    public void testAustralia() throws URISyntaxException, IOException {
        GeoBlock australian = table.getBlock("110.23.24.48");
        Assert.assertNotNull("Expected a block", australian);

        GeoCountry australia = australian.getCountry();
        Assert.assertNotNull("Australian", australian);
        Assert.assertEquals("Expect country code", "AU", australia.getCountryCode());
        Assert.assertEquals("Expect country name", "Australia", australia.getCountryName());
        Assert.assertEquals("Expect continent name", "Oceania", australia.getContinentName());
        Assert.assertEquals("Expect continent code", "OC", australia.getContinentCode());
    }
}
