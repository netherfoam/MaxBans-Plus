package org.maxgamer.maxbans;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.maxgamer.maxbans.locale.Locale;
import org.maxgamer.maxbans.service.GeoIPService;
import org.maxgamer.maxbans.test.UnitTest;
import org.maxgamer.maxbans.util.TemporalDuration;

import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;

/**
 * @author Dirk Jamieson <dirk@redeye.co>
 */
public class LocaleTest implements UnitTest {
    private Locale locale;
    private GeoIPService geoIPService;
    
    @Before
    public void init() {
        Map<String, String> messages = new HashMap<>();
        geoIPService = mock(GeoIPService.class);
        
        messages.put("test.message", "Here is a {{type}} {{mode|message}} {{punc|}}");
        messages.put("ban.kick", "You've been banned by {{source}} for {{reason|no reason}}. Expires: {{duration|never}}");
        messages.put("greeting", "Hello {{planet|person}}");
        locale = new Locale(geoIPService, messages);
    }
    
    @Test
    public void testSubstitution() {
        String expected = "Here is a test message ";
        String got = locale.get()
                .with("type", "test")
                .get("test.message")
                .toString();

        Assert.assertEquals(expected, got);
    }
    
    @Test
    public void testSub2() {
        String expected = "You've been banned by admin for no reason. Expires: 1 Hours";
        String got = locale.get()
                .with("source", "admin")
                .with("duration", new TemporalDuration(ChronoUnit.HOURS.getDuration()))
                .get("ban.kick")
                .toString();
        
        Assert.assertEquals(expected, got);
    }

    @Test
    public void testFallbackSubstitution() {
        String expected = "Hello Sweetie";
        String got = locale.get()
                .with("person", "Sweetie")
                .get("greeting")
                .toString();

        Assert.assertEquals(expected, got);
    }

    @Test
    public void testFallBackWithNoValue() {
        String expected = "Hello person";
        String got = locale.get()
                .get("greeting")
                .toString();

        Assert.assertEquals(expected, got);
    }
}
