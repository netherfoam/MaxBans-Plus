package org.maxgamer.maxbans;

import io.sentry.Sentry;
import io.sentry.SentryClient;
import io.sentry.event.Event;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.flywaydb.core.Flyway;
import org.maxgamer.maxbans.command.*;
import org.maxgamer.maxbans.config.PluginConfig;
import org.maxgamer.maxbans.context.PluginContext;
import org.maxgamer.maxbans.exception.ConfigException;
import org.maxgamer.maxbans.exception.RejectedException;
import org.maxgamer.maxbans.listener.RestrictionListener;
import org.maxgamer.maxbans.locale.Locale;
import org.maxgamer.maxbans.service.MetricService;
import org.maxgamer.maxbans.util.FlywayUtil;
import org.maxgamer.maxbans.util.SentryLogger;

import java.io.File;
import java.util.logging.Logger;

/**
 * @author Dirk Jamieson
 */
public class MaxBansPlus extends JavaPlugin {
    private Locale locale = new Locale();
    private PluginContext context;
    private File messagesFile;
    private Logger sentryLogger;

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
                context.getConfig().load(getConfig(), getServer());
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
     * Migrates the database via flyway
     */
    public void migrate() {
        Flyway flyway = FlywayUtil.migrater(context.getConfig().getJdbcConfig());
        flyway.migrate();
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();

        PluginConfig config;
        try {
            config = new PluginConfig(getConfig(), getServer());
        } catch (ConfigException e) {
            getLogger().severe("Configuration failed validation at " + e.getSection().getCurrentPath() + ": " + e.getMessage());
            getPluginLoader().disablePlugin(this);
            return;
        }

        if(config.isErrorTracking()) {
            // Enabling error tracking means we use a sentry logger
            SentryClient client = Sentry.init("https://34922284faf14712b3a75f86c883349e:18ac8a9a9d6e4dc1a8265daf47d0e223@sentry.io/171230");
            sentryLogger = new SentryLogger(this, Event.Level.WARNING, client);
        }

        context = new PluginContext(config, getServer(), getDataFolder(), new MetricService(this));
        
        migrate();
        
        RestrictionListener restrictionListener = new RestrictionListener(context.getTransactor(), context.getUserService(), context.getLockdownService(), context.getBroadcastService(), context.getAddressService(), locale, sentryLogger);
        getServer().getPluginManager().registerEvents(restrictionListener, this);
        getCommand("ban").setExecutor(new BanCommandExecutor(context.getTransactor(), context.getLocatorService(), context.getUserService(), context.getBroadcastService(), locale, sentryLogger, context.getMetricService()));
        getCommand("ipban").setExecutor(new IPBanCommandExecutor(locale, context.getLocatorService(), context.getTransactor(), context.getAddressService(), context.getUserService(), context.getBroadcastService(), sentryLogger, context.getMetricService()));
        getCommand("unban").setExecutor(new UnbanCommandExecutor(context.getTransactor(), locale, sentryLogger, context.getLocatorService(), context.getAddressService(), context.getBroadcastService(), context.getUserService(), context.getMetricService()));
        getCommand("mute").setExecutor(new MuteCommandExecutor(context.getTransactor(), context.getLocatorService(), context.getUserService(), context.getBroadcastService(), locale, sentryLogger, context.getMetricService()));
        getCommand("ipmute").setExecutor(new IPMuteCommandExecutor(locale, sentryLogger, context.getLocatorService(), context.getTransactor(), context.getAddressService(), context.getUserService(), context.getBroadcastService(), context.getMetricService()));
        getCommand("unmute").setExecutor(new UnmuteCommandExecutor(context.getTransactor(), locale, sentryLogger, context.getLocatorService(), context.getBroadcastService(), context.getAddressService(), context.getUserService(), context.getMetricService()));
        getCommand("iplookup").setExecutor(new LookupCommandExecutor(context.getTransactor(), locale, sentryLogger, context.getLocatorService(), context.getAddressService()));
        getCommand("kick").setExecutor(new KickCommand(context.getTransactor(), locale, sentryLogger, context.getLocatorService(), context.getBroadcastService(), context.getMetricService()));
        getCommand("warn").setExecutor(new WarnCommandExecutor(locale, sentryLogger, context.getTransactor(), context.getLocatorService(), context.getUserService(), context.getWarningService(), context.getBroadcastService(), context.getMetricService()));
        getCommand("lockdown").setExecutor(new LockdownCommandExecutor(context.getTransactor(), locale, sentryLogger, context.getLockdownService(), context.getUserService(), context.getBroadcastService()));

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
