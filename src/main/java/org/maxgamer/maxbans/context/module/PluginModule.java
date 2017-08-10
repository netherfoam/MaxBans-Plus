package org.maxgamer.maxbans.context.module;

import dagger.Module;
import dagger.Provides;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.maxgamer.maxbans.MaxBansPlus;
import org.maxgamer.maxbans.config.PluginConfig;
import org.maxgamer.maxbans.locale.Locale;

import java.util.logging.Logger;

/**
 * @author netherfoam
 */
@Module
public class PluginModule {
    private Logger logger;
    private MaxBansPlus plugin;
    private PluginConfig config;
    private Server server;
    private FileConfiguration configuration;
    private Locale locale;

    public PluginModule(MaxBansPlus plugin, Server server, PluginConfig config, FileConfiguration configuration, Locale locale, Logger logger) {
        this.plugin = plugin;
        this.config = config;
        this.server = server;
        this.configuration = configuration;
        this.locale = locale;
        this.logger = logger;
    }

    @Provides
    public Logger getLogger() {
        return logger;
    }

    @Provides
    public MaxBansPlus getPlugin() {
        return plugin;
    }

    @Provides
    public PluginConfig getConfig() {
        return config;
    }

    @Provides
    public Server getServer() {
        return server;
    }

    @Provides
    public FileConfiguration getConfiguration() {
        return configuration;
    }

    @Provides
    public Locale getLocale() {
        return locale;
    }
}
