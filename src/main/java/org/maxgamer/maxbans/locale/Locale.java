package org.maxgamer.maxbans.locale;

import org.bukkit.configuration.ConfigurationSection;
import org.maxgamer.maxbans.config.PluginConfig;
import org.maxgamer.maxbans.service.GeoIPService;
import org.ocpsoft.prettytime.PrettyTime;
import org.ocpsoft.prettytime.units.JustNow;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Dirk Jamieson <dirk@redeye.co>
 */
public class Locale {
    private HashMap<String, String> messages;
    protected PrettyTime prettyTime = new PrettyTime(java.util.Locale.ENGLISH);
    private GeoIPService geoIPService;
    private PluginConfig pluginConfig;
    
    public Locale(GeoIPService geoIPService){
        this.messages = new HashMap<>();
        this.prettyTime.removeUnit(JustNow.class);
        this.geoIPService = geoIPService;
    }
    
    public Locale(GeoIPService geoIPService, Map<String, String> messages) {
        this(geoIPService);

        this.messages = new HashMap<>(messages);
    }

    public Locale(GeoIPService geoIPService, PluginConfig config, ConfigurationSection messageConfig) {
        this(geoIPService);
        this.pluginConfig = config;

        load(messageConfig);
    }
    
    public void load(ConfigurationSection messageConfig) {
        messages = new HashMap<>();
        for(String key : messageConfig.getKeys(true)) {
            String value = messageConfig.getString(key);

            messages.put(key, value);
        }

        String locale = messageConfig.getString("locale");
        if(locale != null) {
            setLocale(locale);
        }
    }

    public void setLocale(String locale) {
        prettyTime.setLocale(java.util.Locale.forLanguageTag(locale));
    }

    public String getLocale() {
        return prettyTime.getLocale().toString();
    }
    
    public MessageBuilder get() {
        if (pluginConfig != null && pluginConfig.isTooltips()) {
            return new TooltipMessageBuilder(this, geoIPService);
        }

        return new BukkitMessageBuilder(this);
    }

    public Map<String, String> getMessages() {
        return Collections.unmodifiableMap(messages);
    }

    /**
     * Puts the given template message under the given key
     *
     * @param key the key eg "ban.kick"
     * @param template the template message eg "You have been banned for {{duration}}"
     */
    public void put(String key, String template) {
        messages.put(key, template);
    }

    /**
     * Returns true if this locale has the given message defined
     *
     * @param message the message name eg, "ban.message"
     * @return true if the the exists, false if the message is undefined
     */
    public boolean has(String message) {
        return messages.containsKey(message);
    }
}
