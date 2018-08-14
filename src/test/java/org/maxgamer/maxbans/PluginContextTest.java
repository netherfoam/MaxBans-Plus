package org.maxgamer.maxbans;

import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.junit.After;
import org.junit.Before;
import org.maxgamer.maxbans.config.PluginConfig;
import org.maxgamer.maxbans.context.PluginContext;
import org.maxgamer.maxbans.locale.Locale;
import org.maxgamer.maxbans.repository.H2Test;
import org.maxgamer.maxbans.test.IntegrationTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * @author Dirk Jamieson <dirk@redeye.co>
 */
public class PluginContextTest extends H2Test implements IntegrationTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(PluginContextTest.class);
    private PluginContext context;
    
    @Before
    public void init() throws IOException, InterruptedException {
        super.init();

        PluginConfig config = new PluginConfig();
        config.setJdbcConfig(getJdbc());
        Server server = mock(Server.class);
        File folder = new File("maxbans");
        if(!folder.isDirectory() && !folder.mkdir()) {
            throw new IllegalStateException("Can't create data folder");
        }

        MaxBansPlus plugin = mock(MaxBansPlus.class);
        FileConfiguration configuration = mock(FileConfiguration.class);
        doReturn(configuration).when(plugin).getConfig();
        Locale locale = new Locale();
        YamlConfiguration localeConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("messages.yml")));
        locale.load(localeConfig);

        PluginManager pluginManager = mock(PluginManager.class);

        context = new PluginContext(plugin, config, locale, server, folder, java.util.logging.Logger.getLogger("Test"), pluginManager);

        LOGGER.info("...Constructed plugin context");
    }

    @After
    public void destroy() {
        LOGGER.info("Destroying context...");
        if(context == null) return;
        context.close();

        File[] files = context.getDataFolder().listFiles();
        if (files == null) return;

        if(files != null) {
            for (File f : files) {
                f.delete();
            }
        }

        context.getDataFolder().delete();
    }

    public PluginContext getContext() {
        return context;
    }
}
