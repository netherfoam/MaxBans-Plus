package org.maxgamer.maxbans.context.module;

import dagger.Module;
import dagger.Provides;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.maxgamer.maxbans.MaxBansPlus;
import org.maxgamer.maxbans.config.PluginConfig;
import org.maxgamer.maxbans.locale.Locale;

import javax.inject.Singleton;
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
    @Singleton
    public Logger getLogger() {
        return logger;
    }

    @Provides
    @Singleton
    public MaxBansPlus getPlugin() {
        return plugin;
    }

    @Provides
    @Singleton
    public PluginConfig getConfig() {
        return config;
    }

    @Provides
    @Singleton
    public Server getServer() {
        return server;
    }

    @Provides
    @Singleton
    public FileConfiguration getConfiguration() {
        return configuration;
    }

    @Provides
    @Singleton
    public Locale getLocale() {
        return locale;
    }
}
