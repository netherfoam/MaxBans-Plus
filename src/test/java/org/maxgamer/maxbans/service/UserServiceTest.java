package org.maxgamer.maxbans.service;

import org.bukkit.plugin.PluginManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.maxgamer.maxbans.PluginContextTest;
import org.maxgamer.maxbans.event.BanUserEvent;
import org.maxgamer.maxbans.event.MuteUserEvent;
import org.maxgamer.maxbans.event.UnbanUserEvent;
import org.maxgamer.maxbans.event.UnmuteUserEvent;
import org.maxgamer.maxbans.exception.CancelledException;
import org.maxgamer.maxbans.exception.RejectedException;
import org.maxgamer.maxbans.orm.User;
import org.maxgamer.maxbans.test.IntegrationTest;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Dirk Jamieson <dirk@redeye.co>
 */
public class UserServiceTest extends PluginContextTest implements IntegrationTest {
    private PluginManager pluginManager;

    @Before
    public void setup() {
        pluginManager = getContext().getPluginModule().getPluginManager();
    }

    @Test
    public void testBan() throws RejectedException, CancelledException {
        UserService users = getContext().components().services().user();
        User user = users.create(UUID.randomUUID(), "Test_McGee", Instant.now());

        Assert.assertNull("Expect user to be unbanned", users.getBan(user));
        users.ban(null, user, "Breaking Rules", null);
        Assert.assertNotNull("Expect user to be banned", users.getBan(user));

        verify(pluginManager, times(1)).callEvent(isA(BanUserEvent.class));
    }
    
    @Test
    public void testMute() throws RejectedException, CancelledException {
        UserService users = getContext().components().services().user();
        User user = users.create(UUID.randomUUID(), "Test_McGee", Instant.now());

        Assert.assertNull("Expect user to be unmuted", users.getMute(user));
        users.mute(null, user, "Breaking Rules", null);
        Assert.assertNotNull("Expect user to be muted", users.getMute(user));

        verify(pluginManager, times(1)).callEvent(isA(MuteUserEvent.class));
    }

    @Test
    public void testUnmute() throws RejectedException, CancelledException {
        UserService users = getContext().components().services().user();
        User user = users.create(UUID.randomUUID(), "Test_McGee", Instant.now());

        users.mute(null, user, "Breaking Rules", null);
        users.unmute(null, user);
        Assert.assertNull("Expect user to be unmuted", users.getMute(user));

        verify(pluginManager, times(1)).callEvent(isA(UnmuteUserEvent.class));
    }

    @Test
    public void testUnban() throws RejectedException, CancelledException {
        UserService users = getContext().components().services().user();
        User user = users.create(UUID.randomUUID(), "Test_McGee", Instant.now());

        users.ban(null, user, "Breaking Rules", null);
        users.unban(null, user);
        Assert.assertNull("Expect user to be unbanned", users.getBan(user));

        verify(pluginManager, times(1)).callEvent(isA(UnbanUserEvent.class));
    }
}
