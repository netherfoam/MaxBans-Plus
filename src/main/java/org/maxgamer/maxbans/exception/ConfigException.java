package org.maxgamer.maxbans.exception;

import org.bukkit.configuration.ConfigurationSection;

/**
 * @author netherfoam
 */
public class ConfigException extends Exception {
    private ConfigurationSection section;

    public ConfigException(ConfigurationSection section, String s) {
        super(s);
        this.section = section;
    }

    public ConfigurationSection getSection() {
        return section;
    }
}
