package org.maxgamer.maxbans.util;

import io.sentry.SentryClient;
import io.sentry.event.Event;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.junit.Before;
import org.junit.Test;
import org.maxgamer.maxbans.test.IntegrationTest;

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * @author netherfoam
 */
public class SentryLoggerTest implements IntegrationTest {
    private Plugin plugin;

    @Before
    public void init() {
        plugin = mock(Plugin.class);
        Server server = mock(Server.class);
        Logger serverLogger = Logger.getLogger("parent");
        doReturn(serverLogger).when(server).getLogger();

        PluginDescriptionFile description = new PluginDescriptionFile("plugin", "1", "org.maxgamer.plugin.Plugger");
        doReturn(description).when(plugin).getDescription();
        doReturn(server).when(plugin).getServer();
    }

    @Test
    public void run() {
        SentryClient client = mock(SentryClient.class);
        SentryLogger logger = new SentryLogger(null, "SentryLoggerTest", "platform", "release", "serverName", Event.Level.WARNING, client);

        RuntimeException ex = new RuntimeException();
        ex.fillInStackTrace();

        logger.log(Level.SEVERE, "Hello World", ex);
        verify(client, times(1)).sendEvent(any(Event.class));

        reset(client);

        logger.log(Level.INFO, "Hello World", ex);
        verify(client, never()).sendEvent(any(Event.class));
    }
}
