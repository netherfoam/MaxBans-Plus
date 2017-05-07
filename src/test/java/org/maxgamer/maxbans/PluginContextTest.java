package org.maxgamer.maxbans;

import org.bukkit.Server;
import org.junit.Before;
import org.maxgamer.maxbans.config.PluginConfig;
import org.maxgamer.maxbans.context.PluginContext;
import org.maxgamer.maxbans.repository.H2Test;
import org.maxgamer.maxbans.test.IntegrationTest;

import static org.mockito.Mockito.mock;

/**
 * @author Dirk Jamieson <dirk@redeye.co>
 */
public class PluginContextTest extends H2Test implements IntegrationTest {
    private PluginContext context;
    
    @Before
    public void init() {
        super.init();

        PluginConfig config = new PluginConfig();
        config.setJdbcConfig(getJdbc());
        Server server = mock(Server.class);
        context = new PluginContext(config, server);
    }

    public PluginContext getContext() {
        return context;
    }
}
