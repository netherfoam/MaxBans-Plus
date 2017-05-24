package org.maxgamer.maxbans.service;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.junit.Test;

import java.time.Duration;
import java.util.Collections;

import static org.mockito.Mockito.*;

/**
 * @author netherfoam
 */
public class BroadcastServiceTest {
    /**
     * Ensure that sending multiple messages to moderators are throttled by the broadcast system when required
     */
    @Test
    public void testFirewalled() {
        Server server = mock(Server.class);
        BroadcastService broadcast = new BroadcastService(server);

        Player player = mock(Player.class);
        doReturn(true).when(player).hasPermission(any(String.class));
        doReturn(Collections.singletonList(player)).when(server).getOnlinePlayers();

        broadcast.moderators("test", Duration.ofMinutes(1), "Hello");
        broadcast.moderators("test", Duration.ofMinutes(1), "Hello");

        verify(server, times(1)).broadcast(eq("Hello"), any());
    }
}
