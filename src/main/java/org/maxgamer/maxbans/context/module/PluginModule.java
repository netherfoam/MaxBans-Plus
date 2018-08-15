package org.maxgamer.maxbans.context.module;

import dagger.Module;
import dagger.Provides;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
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
    private PluginManager pluginManager;
    private boolean sessionInitialised = false;

    public PluginModule(MaxBansPlus plugin, Server server, PluginConfig config, FileConfiguration configuration, Locale locale, Logger logger, PluginManager pluginManager) {
        this.plugin = plugin;
        this.config = config;
        this.server = server;
        this.configuration = configuration;
        this.locale = locale;
        this.logger = logger;
        this.pluginManager = pluginManager;
    }

    @Provides
    @Singleton
    public PluginModule pluginModule() {
        return this;
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

    @Provides
    @Singleton
    public PluginManager getPluginManager() {
        return pluginManager;
    }

    public boolean isSessionInitialised() {
        return sessionInitialised;
    }

    public void setSessionInitialised(boolean initialised) {
        this.sessionInitialised = initialised;
    }
}
