package org.maxgamer.maxbans.config;

import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Dirk Jamieson <dirk@redeye.co>
 */
public class JdbcConfig {
    private String url;
    private String driver;
    private String username;
    private String password;

    public JdbcConfig() {
    }

    public JdbcConfig(ConfigurationSection section) {
        setUrl(section.getString("url", "jdbc:h2:mem:"));
        setDriver(section.getString("driver", "org.h2.Driver"));
        setUsername(section.getString("username", "root"));
        setPassword(section.getString("password", "password"));
    }

    public String getDriver() {
        return driver;
    }

    public JdbcConfig setDriver(String driver) {
        this.driver = driver;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public JdbcConfig setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public JdbcConfig setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public JdbcConfig setPassword(String password) {
        this.password = password;
        return this;
    }
}
