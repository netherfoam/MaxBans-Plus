package org.maxgamer.maxbans;

import io.sentry.Sentry;
import io.sentry.SentryClient;
import io.sentry.event.Event;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.flywaydb.core.Flyway;
import org.maxgamer.maxbans.config.PluginConfig;
import org.maxgamer.maxbans.context.PluginContext;
import org.maxgamer.maxbans.exception.ConfigException;
import org.maxgamer.maxbans.locale.Locale;
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

        context = new PluginContext(this, config, locale, getServer(), getDataFolder(), getErrorLogger());
        
        migrate();

        // TODO
        /*RestrictionListener restrictionListener = context.getInjector().getInstance(RestrictionListener.class);
        getServer().getPluginManager().registerEvents(restrictionListener, this);

        getCommand("ban").setExecutor(context.getInjector().getInstance(BanCommandExecutor.class));
        getCommand("ipban").setExecutor(context.getInjector().getInstance(IPBanCommandExecutor.class));
        getCommand("unban").setExecutor(context.getInjector().getInstance(UnbanCommandExecutor.class));
        getCommand("mute").setExecutor(context.getInjector().getInstance(MuteCommandExecutor.class));
        getCommand("ipmute").setExecutor(context.getInjector().getInstance(IPMuteCommandExecutor.class));
        getCommand("unmute").setExecutor(context.getInjector().getInstance(UnmuteCommandExecutor.class));
        getCommand("iplookup").setExecutor(context.getInjector().getInstance(LookupCommandExecutor.class));
        getCommand("kick").setExecutor(context.getInjector().getInstance(KickCommandExecutor.class));
        getCommand("warn").setExecutor(context.getInjector().getInstance(WarnCommandExecutor.class));
        getCommand("lockdown").setExecutor(context.getInjector().getInstance(LockdownCommandExecutor.class));

        context.getInjector().getInstance(Transactor.class).work(session -> {
            for(Player player : context.getServer().getOnlinePlayers()) {
                try {
                    restrictionListener.onJoin(player, player.getAddress().getAddress().getHostAddress());
                } catch (RejectedException e) {
                    player.kickPlayer(e.getMessage(locale));
                }
            }
        });*/
    }

    @Override
    public void onDisable() {
        if(context != null) {
            // TODO?
            /*context.close();*/
        }
    }

    public PluginContext getContext() {
        return context;
    }

    public Locale getLocale() {
        return locale;
    }

    public Logger getErrorLogger() {
        if(sentryLogger == null) {
            // Just use the regular plugin logger
            return getLogger();
        }

        // Sentry logger for errors
        return sentryLogger;
    }
}
