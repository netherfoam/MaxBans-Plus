package org.maxgamer.maxbans.config;

import org.bukkit.configuration.Configuration;

/**
 * @author Dirk Jamieson <dirk@redeye.co>
 */
public class PluginConfig {
    private JdbcConfig jdbcConfig;
    private boolean isOffline;
    
    public PluginConfig() {
        
    }
    
    public PluginConfig(Configuration configuration) {
        this();
        
        load(configuration);
    }
    
    public void load(Configuration configuration) {
        this.setJdbcConfig(new JdbcConfig(configuration.getConfigurationSection("database")));
        this.setOffline(configuration.getBoolean("offline", false));
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
}
