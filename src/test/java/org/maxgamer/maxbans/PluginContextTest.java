package org.maxgamer.maxbans;

import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.After;
import org.junit.Before;
import org.maxgamer.maxbans.config.PluginConfig;
import org.maxgamer.maxbans.context.PluginContext;
import org.maxgamer.maxbans.locale.Locale;
import org.maxgamer.maxbans.repository.H2Test;
import org.maxgamer.maxbans.test.IntegrationTest;

import java.io.File;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import static org.mockito.Mockito.doReturn;
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

        context = new PluginContext(plugin, config, locale, server, folder, Logger.getLogger("Test"));
    }

    @After
    public void destroy() {
        if(context == null) return;
        if(context.getDataFolder() == null) return;

        File[] files = context.getDataFolder().listFiles();
        if(files != null) {
            for (File f : context.getDataFolder().listFiles()) {
                f.delete();
            }
        }

        context.getDataFolder().delete();
    }

    public PluginContext getContext() {
        return context;
    }
}
