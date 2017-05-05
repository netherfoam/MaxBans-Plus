package org.maxgamer.maxbans;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.flywaydb.core.Flyway;
import org.maxgamer.maxbans.command.BanCommandExecutor;
import org.maxgamer.maxbans.command.MuteCommandExecutor;
import org.maxgamer.maxbans.config.JdbcConfig;
import org.maxgamer.maxbans.config.PluginConfig;
import org.maxgamer.maxbans.context.PluginContext;
import org.maxgamer.maxbans.listener.JoinListener;
import org.maxgamer.maxbans.locale.Locale;

import java.io.File;

/**
 * @author Dirk Jamieson
 */
public class MaxBans extends JavaPlugin {
    private Locale locale = new Locale();
    private PluginContext context;
    private File messagesFile;

    @Override
    public void onLoad() {
        messagesFile = new File(getDataFolder(), "messages.yml");
    }
    
    @Override
    public void saveDefaultConfig() {
        super.saveDefaultConfig();
        
        if(!this.messagesFile.exists()) {
            this.saveResource(messagesFile.getName(), false);
        }
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        
        if(context != null) {
            context.getConfig().load(getConfig());
        }
        
        ConfigurationSection localeConfig = YamlConfiguration.loadConfiguration(messagesFile);
        locale.load(localeConfig);
    }

    /**
     * Migrates flyway. If the database is not empty and no schema version is detected,
     * this will raise an exception.
     */
    public void migrate() {
        Flyway flyway = new Flyway();
        JdbcConfig jdbc = context.getConfig().getJdbcConfig();
        
        flyway.setClassLoader(getClass().getClassLoader());
        flyway.setDataSource(jdbc.getUrl(), jdbc.getUsername(), jdbc.getPassword());
        
        flyway.migrate();
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();

        PluginConfig config = new PluginConfig(getConfig());
        context = new PluginContext(config, getServer());
        
        migrate();
        
        JoinListener joinListener = new JoinListener(context.getUserService());
        
        getServer().getPluginManager().registerEvents(joinListener, this);
        getCommand("ban").setExecutor(new BanCommandExecutor(getServer(), context.getUserService(), context.getBroadcastService(), locale));
        getCommand("mute").setExecutor(new MuteCommandExecutor(getServer(), context.getUserService(), context.getBroadcastService(), locale));
    }

    @Override
    public void onDisable() {
        
    }
}
