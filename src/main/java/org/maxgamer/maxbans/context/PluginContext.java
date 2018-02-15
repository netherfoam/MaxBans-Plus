package org.maxgamer.maxbans.context;

import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.maxgamer.maxbans.MaxBansPlus;
import org.maxgamer.maxbans.config.PluginConfig;
import org.maxgamer.maxbans.context.component.DaggerPluginComponent;
import org.maxgamer.maxbans.context.component.PluginComponent;
import org.maxgamer.maxbans.context.module.PluginModule;
import org.maxgamer.maxbans.locale.Locale;

import java.io.File;
import java.util.logging.Logger;

/**
 * @author Dirk Jamieson <dirk@redeye.co>
 */
public class PluginContext {
    private PluginConfig config;
    private Server server;
    private File dataFolder;
    private PluginComponent modules;
    private PluginModule pluginModule;
    
    public PluginContext(MaxBansPlus plugin, PluginConfig config, Locale locale, Server server, File dataFolder, Logger logger) {
        this.config = config;
        this.server = server;
        this.dataFolder = dataFolder;

        FileConfiguration lockdownCfg = YamlConfiguration.loadConfiguration(new File(dataFolder, "lockdown.yml"));

        pluginModule = new PluginModule(plugin, server, config, lockdownCfg, locale, logger);

        modules = DaggerPluginComponent
                .builder()
                .pluginModule(pluginModule)
                .build();
    }

    public PluginComponent components() {
        return modules;
    }

    public PluginConfig getConfig() {
        return config;
    }

    public Server getServer() {
        return server;
    }

    public File getDataFolder() {
        return dataFolder;
    }

    public void close() {
        if (pluginModule.isSessionInitialised()) {
            components().sessionFactory().close();
        }
    }
}
