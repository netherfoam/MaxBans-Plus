package org.maxgamer.maxbans;

import org.junit.Before;
import org.maxgamer.maxbans.config.PluginConfig;
import org.maxgamer.maxbans.context.PluginContext;
import org.maxgamer.maxbans.repository.H2Test;

/**
 * @author Dirk Jamieson <dirk@redeye.co>
 */
public class PluginContextTest extends H2Test {
    private PluginContext context;
    
    @Before
    public void init() {
        super.init();

        PluginConfig config = new PluginConfig();
        config.setJdbcConfig(getJdbc());
        context = new PluginContext(config, null);
    }

    public PluginContext getContext() {
        return context;
    }
}
