package org.maxgamer.maxbans;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.flywaydb.core.Flyway;
import org.maxgamer.maxbans.command.*;
import org.maxgamer.maxbans.config.JdbcConfig;
import org.maxgamer.maxbans.config.PluginConfig;
import org.maxgamer.maxbans.context.PluginContext;
import org.maxgamer.maxbans.exception.ConfigException;
import org.maxgamer.maxbans.exception.RejectedException;
import org.maxgamer.maxbans.listener.RestrictionListener;
import org.maxgamer.maxbans.locale.Locale;
import org.maxgamer.maxbans.service.MetricService;

import java.io.File;

/**
 * @author Dirk Jamieson
 */
public class MaxBansPlus extends JavaPlugin {
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
            try {
                context.getConfig().load(getConfig());
            } catch (ConfigException e) {
                getLogger().severe("Configuration failed validation at " + e.getSection().getCurrentPath() + ": " + e.getMessage());
                getPluginLoader().disablePlugin(this);
                return;
            }
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

        PluginConfig config;
        try {
            config = new PluginConfig(getConfig());
        } catch (ConfigException e) {
            getLogger().severe("Configuration failed validation at " + e.getSection().getCurrentPath() + ": " + e.getMessage());
            getPluginLoader().disablePlugin(this);
            return;
        }

        context = new PluginContext(config, getServer(), getDataFolder(), new MetricService(this));
        
        migrate();
        
        RestrictionListener restrictionListener = new RestrictionListener(context.getTransactor(), context.getUserService(), context.getLockdownService(), context.getBroadcastService(), context.getAddressService(), locale);
        getServer().getPluginManager().registerEvents(restrictionListener, this);
        getCommand("ban").setExecutor(new BanCommandExecutor(context.getTransactor(), context.getLocatorService(), context.getUserService(), context.getBroadcastService(), locale, context.getMetricService()));
        getCommand("ipban").setExecutor(new IPBanCommandExecutor(locale, context.getLocatorService(), context.getTransactor(), context.getAddressService(), context.getUserService(), context.getBroadcastService(), context.getMetricService()));
        getCommand("unban").setExecutor(new UnbanCommandExecutor(context.getTransactor(), locale, context.getLocatorService(), context.getAddressService(), context.getBroadcastService(), context.getUserService(), context.getMetricService()));
        getCommand("mute").setExecutor(new MuteCommandExecutor(context.getTransactor(), context.getLocatorService(), context.getUserService(), context.getBroadcastService(), locale, context.getMetricService()));
        getCommand("ipmute").setExecutor(new IPMuteCommandExecutor(locale, context.getLocatorService(), context.getTransactor(), context.getAddressService(), context.getUserService(), context.getBroadcastService(), context.getMetricService()));
        getCommand("unmute").setExecutor(new UnmuteCommandExecutor(context.getTransactor(), locale, context.getLocatorService(), context.getBroadcastService(), context.getAddressService(), context.getUserService(), context.getMetricService()));
        getCommand("iplookup").setExecutor(new IPLookupCommandExecutor(context.getTransactor(), locale, context.getLocatorService(), context.getAddressService()));
        getCommand("kick").setExecutor(new KickCommand(context.getTransactor(), locale, context.getLocatorService(), context.getBroadcastService(), context.getMetricService()));
        getCommand("warn").setExecutor(new WarnCommandExecutor(locale, context.getTransactor(), context.getLocatorService(), context.getUserService(), context.getWarningService(), context.getBroadcastService(), context.getMetricService()));
        getCommand("lockdown").setExecutor(new LockdownCommandExecutor(context.getTransactor(), locale, context.getLockdownService(), context.getUserService(), context.getBroadcastService()));

        context.getTransactor().work(session -> {
            for(Player player : context.getServer().getOnlinePlayers()) {
                try {
                    restrictionListener.onJoin(player, player.getAddress().getAddress().getHostAddress());
                } catch (RejectedException e) {
                    player.kickPlayer(e.getMessage(locale));
                }
            }
        });
    }

    @Override
    public void onDisable() {
        if(context != null) {
            context.close();
        }
    }

    public PluginContext getContext() {
        return context;
    }

    public Locale getLocale() {
        return locale;
    }
}
