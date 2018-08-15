package org.maxgamer.maxbans.context;

import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.hibernate.SessionFactory;
import org.hibernate.c3p0.internal.C3P0ConnectionProvider;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.internal.SessionFactoryImpl;
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
    
    public PluginContext(MaxBansPlus plugin, PluginConfig config, Locale locale, Server server, File dataFolder, Logger logger, PluginManager pluginManager) {
        this.config = config;
        this.server = server;
        this.dataFolder = dataFolder;

        FileConfiguration lockdownCfg = YamlConfiguration.loadConfiguration(new File(dataFolder, "lockdown.yml"));

        pluginModule = new PluginModule(plugin, server, config, lockdownCfg, locale, logger, pluginManager);

        modules = DaggerPluginComponent
                .builder()
                .pluginModule(pluginModule)
                .build();
    }

    public PluginModule getPluginModule() {
        return pluginModule;
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
            SessionFactory factory = components().sessionFactory();

            if (factory instanceof SessionFactoryImpl) {
                SessionFactoryImpl sf = (SessionFactoryImpl) factory;
                ConnectionProvider provider = sf.getServiceRegistry().getService(ConnectionProvider.class);
                if (provider instanceof C3P0ConnectionProvider) {
                    ((C3P0ConnectionProvider) provider).stop();
                }
            }
            factory.close();
        }
    }
}
