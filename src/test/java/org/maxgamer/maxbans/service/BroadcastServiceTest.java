package org.maxgamer.maxbans.service;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.maxgamer.maxbans.locale.Locale;

import java.time.Duration;
import java.util.Collections;

import static org.mockito.Mockito.*;

/**
 * @author netherfoam
 */
public class BroadcastServiceTest {
    private Server server;
    private Locale locale;
    private BroadcastService broadcast;

    @Before
    public void init() {
        server = mock(Server.class);
        locale = new Locale();

        broadcast = new BroadcastService(server, locale);
    }

    /**
     * Ensure that sending multiple messages to moderators are throttled by the broadcast system when required
     */
    @Test
    public void testFirewalled() {
        Player player = mock(Player.class);
        doReturn(true).when(player).hasPermission(any(String.class));
        doReturn(Collections.singletonList(player)).when(server).getOnlinePlayers();

        broadcast.moderators("test", Duration.ofMinutes(1), "Hello");
        broadcast.moderators("test", Duration.ofMinutes(1), "Hello");

        verify(server, times(1)).broadcast(eq("Hello"), any());
    }

    /**
     * Ensure that our messages are prefixed and suffixed with the silent marker when sending silent messages
     */
    @Test
    public void testSilencePrefixSuffix() {
        locale.put("silent.prefix", "PRE");
        locale.put("silent.suffix", "SUF");

        broadcast.broadcast("test", true);

        verify(server, times(1)).broadcast(matches("PRE.*SUF"), anyString());
    }
}
