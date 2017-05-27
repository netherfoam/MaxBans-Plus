package org.maxgamer.maxbans.config;

import org.bukkit.Server;
import org.bukkit.configuration.Configuration;
import org.maxgamer.maxbans.exception.ConfigException;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Dirk Jamieson <dirk@redeye.co>
 */
public class PluginConfig {
    private Server server;
    private JdbcConfig jdbcConfig;
    private WarningConfig warningConfig;
    private boolean isOffline;
    private Set<String> chatCommands;
    
    public PluginConfig() {
        setJdbcConfig(new JdbcConfig());
        setWarningConfig(new WarningConfig());
        setOffline(false);
    }
    
    public PluginConfig(Configuration configuration, Server server) throws ConfigException {
        load(configuration, server);
    }
    
    public void load(Configuration configuration, Server server) throws ConfigException {
        this.setJdbcConfig(new JdbcConfig(configuration.getConfigurationSection("database")));
        this.setWarningConfig(new WarningConfig(configuration.getConfigurationSection("warnings")));
        this.setOffline(configuration.getBoolean("offline", !server.getOnlineMode()));
        this.setChatCommands(configuration.getStringList("chat-commands"));
    }

    public JdbcConfig getJdbcConfig() {
        return jdbcConfig;
    }

    public PluginConfig setJdbcConfig(JdbcConfig jdbcConfig) {
        this.jdbcConfig = jdbcConfig;
        return this;
    }

    public WarningConfig getWarningConfig() {
        return warningConfig;
    }

    public PluginConfig setWarningConfig(WarningConfig warningConfig) {
        this.warningConfig = warningConfig;
        return this;
    }

    public boolean isOffline() {
        return isOffline;
    }

    public PluginConfig setOffline(boolean offline) {
        isOffline = offline;
        return this;
    }

    public Set<String> getChatCommands() {
        return chatCommands;
    }

    public void setChatCommands(Collection<String> chatCommands) {
        this.chatCommands = new HashSet<>(chatCommands.size());
        for(String command : chatCommands) {
            if(command == null) continue;
            this.chatCommands.add(command.toLowerCase());
        }
    }
}
