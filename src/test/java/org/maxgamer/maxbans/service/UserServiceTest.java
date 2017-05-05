package org.maxgamer.maxbans.service;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.maxgamer.maxbans.PluginContextTest;
import org.maxgamer.maxbans.config.PluginConfig;
import org.maxgamer.maxbans.context.PluginContext;
import org.maxgamer.maxbans.exception.RejectedException;
import org.maxgamer.maxbans.orm.User;
import org.maxgamer.maxbans.service.UserService;

import java.time.Instant;
import java.util.UUID;

/**
 * @author Dirk Jamieson <dirk@redeye.co>
 */
public class UserServiceTest extends PluginContextTest {
    @Test
    public void testBan() throws RejectedException {
        UserService users = getContext().getUserService();
        User user = users.create(UUID.randomUUID(), "Test_McGee", Instant.now());

        Assert.assertFalse("Expect user to be unbanned", users.isBanned(user));
        users.ban(null, user, "Breaking Rules", null);
        Assert.assertTrue("Expect user to be banned", users.isBanned(user));
    }
    
    @Test
    public void testMute() throws RejectedException {
        UserService users = getContext().getUserService();
        User user = users.create(UUID.randomUUID(), "Test_McGee", Instant.now());

        Assert.assertFalse("Expect user to be unbanned", users.isMuted(user));
        users.mute(null, user, "Breaking Rules", null);
        Assert.assertTrue("Expect user to be banned", users.isMuted(user));
    }
}
