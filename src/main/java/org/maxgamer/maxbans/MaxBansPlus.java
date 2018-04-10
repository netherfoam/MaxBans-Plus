package org.maxgamer.maxbans;

import io.sentry.Sentry;
import io.sentry.SentryClient;
import io.sentry.event.Event;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.maxgamer.maxbans.config.PluginConfig;
import org.maxgamer.maxbans.context.PluginContext;
import org.maxgamer.maxbans.context.component.CommandExecutorComponent;
import org.maxgamer.maxbans.exception.ConfigException;
import org.maxgamer.maxbans.exception.SchemaBrokenException;
import org.maxgamer.maxbans.exception.RejectedException;
import org.maxgamer.maxbans.locale.Locale;
import org.maxgamer.maxbans.transaction.TransactionLayer;
import org.maxgamer.maxbans.util.FlywayUtil;
import org.maxgamer.maxbans.util.SentryLogger;

import java.io.*;
import java.util.logging.Level;
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

        YamlConfiguration localeConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(getResource("messages.yml")));
        try {
            localeConfig.load(new FileReader(messagesFile));
        } catch (IOException e) {
            throw new IllegalStateException("Couldn't read file: " + messagesFile, e);
        } catch (InvalidConfigurationException e) {
            throw new IllegalStateException("Bad YML configuration file: " + messagesFile, e);
        }

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

        try {
            // Update our database if necessary
            migrate();
        } catch (SchemaBrokenException e) {
            // This error is a problem that the server admin should fix
            getLogger().log(Level.SEVERE, e.getMessage());
            getPluginLoader().disablePlugin(this);
            return;
        } catch (FlywayException e) {
            getErrorLogger().log(Level.SEVERE, "Unable to migrate database. Disabling MaxBans", e);
            getPluginLoader().disablePlugin(this);
            return;
        }

        // Register our listener
        register(context.components().listeners().restriction());

        // Register our commands
        CommandExecutorComponent commands = context.components().commands();
        register("ban", commands.ban());
        register("ipban", commands.ipban());
        register("unban", commands.unban());
        register("mute", commands.mute());
        register("ipmute", commands.ipmute());
        register("unmute", commands.unmute());
        register("iplookup", commands.lookup());
        register("kick", commands.kick());
        register("warn", commands.warn());
        register("lockdown", commands.lockdown());
        register("history", commands.history());

        // Kick any players who aren't allowed to be on the server right now
        try (TransactionLayer tx = context.components().transactor().transact()) {
            for(Player player : context.getServer().getOnlinePlayers()) {
                try {
                    context.components().listeners().restriction().onJoin(player, player.getAddress().getAddress().getHostAddress());
                } catch (RejectedException e) {
                    player.kickPlayer(e.getMessage(locale));
                }
            }
        }
    }

    private void register(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    private void register(String name, CommandExecutor command) {
        getCommand(name).setExecutor(command);
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

    public Logger getErrorLogger() {
        if(sentryLogger == null) {
            // Just use the regular plugin logger
            return getLogger();
        }

        // Sentry logger for errors
        return sentryLogger;
    }
}
