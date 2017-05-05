package org.maxgamer.maxbans;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.maxgamer.maxbans.locale.Locale;
import org.maxgamer.maxbans.util.TemporalDuration;

import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Dirk Jamieson <dirk@redeye.co>
 */
public class LocaleTest {
    private Locale locale;
    
    @Before
    public void init() {
        Map<String, String> messages = new HashMap<>();
        
        messages.put("test.message", "Here is a {{type}} {{mode|message}} {{punc|}}");
        messages.put("ban.kick", "You've been banned by {{source}} for {{reason|no reason}}. Expires: {{duration|never}}");
        locale = new Locale(messages);
    }
    
    @Test
    public void testSubstitution() {
        String expected = "Here is a test message ";
        String got = locale.get()
                .with("type", "test")
                .get("test.message");

        Assert.assertEquals(expected, got);
    }
    
    @Test
    public void testSub2() {
        String expected = "You've been banned by admin for no reason. Expires: 1 Hours";
        String got = locale.get()
                .with("source", "admin")
                .with("duration", new TemporalDuration(ChronoUnit.HOURS.getDuration()))
                .get("ban.kick");
        
        Assert.assertEquals(expected, got);
    }
}
