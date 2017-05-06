package org.maxgamer.maxbans.config;

import org.bukkit.configuration.Configuration;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Dirk Jamieson <dirk@redeye.co>
 */
public class PluginConfig {
    private JdbcConfig jdbcConfig;
    private boolean isOffline;
    private Set<String> chatCommands;
    
    public PluginConfig() {
        
    }
    
    public PluginConfig(Configuration configuration) {
        this();
        
        load(configuration);
    }
    
    public void load(Configuration configuration) {
        this.setJdbcConfig(new JdbcConfig(configuration.getConfigurationSection("database")));
        this.setOffline(configuration.getBoolean("offline", false));
        this.setChatCommands(configuration.getStringList("chat-commands"));
    }

    public JdbcConfig getJdbcConfig() {
        return jdbcConfig;
    }

    public PluginConfig setJdbcConfig(JdbcConfig jdbcConfig) {
        this.jdbcConfig = jdbcConfig;
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
