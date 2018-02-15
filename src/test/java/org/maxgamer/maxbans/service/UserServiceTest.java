package org.maxgamer.maxbans.service;

import junit.framework.Assert;
import org.junit.Test;
import org.maxgamer.maxbans.PluginContextTest;
import org.maxgamer.maxbans.exception.RejectedException;
import org.maxgamer.maxbans.orm.User;
import org.maxgamer.maxbans.test.IntegrationTest;

import java.time.Instant;
import java.util.UUID;

/**
 * @author Dirk Jamieson <dirk@redeye.co>
 */
public class UserServiceTest extends PluginContextTest implements IntegrationTest {
    @Test
    public void testBan() throws RejectedException {
        UserService users = getContext().components().services().user();
        User user = users.create(UUID.randomUUID(), "Test_McGee", Instant.now());

        Assert.assertNull("Expect user to be unbanned", users.getBan(user));
        users.ban(null, user, "Breaking Rules", null);
        Assert.assertNotNull("Expect user to be banned", users.getBan(user));
    }
    
    @Test
    public void testMute() throws RejectedException {
        UserService users = getContext().components().services().user();
        User user = users.create(UUID.randomUUID(), "Test_McGee", Instant.now());

        Assert.assertNull("Expect user to be unmuted", users.getMute(user));
        users.mute(null, user, "Breaking Rules", null);
        Assert.assertNotNull("Expect user to be muted", users.getMute(user));
    }

    @Test
    public void testUnmute() throws RejectedException {
        UserService users = getContext().components().services().user();
        User user = users.create(UUID.randomUUID(), "Test_McGee", Instant.now());

        users.mute(null, user, "Breaking Rules", null);
        users.unmute(null, user);
        Assert.assertNull("Expect user to be unmuted", users.getMute(user));
    }

    @Test
    public void testUnban() throws RejectedException {
        UserService users = getContext().components().services().user();
        User user = users.create(UUID.randomUUID(), "Test_McGee", Instant.now());

        users.ban(null, user, "Breaking Rules", null);
        users.unban(null, user);
        Assert.assertNull("Expect user to be unbanned", users.getBan(user));
    }
}
