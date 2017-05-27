package org.maxgamer.maxbans.locale;

import org.bukkit.configuration.ConfigurationSection;
import org.ocpsoft.prettytime.PrettyTime;
import org.ocpsoft.prettytime.units.JustNow;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Dirk Jamieson <dirk@redeye.co>
 */
public class Locale {
    protected HashMap<String, String> messages;
    protected PrettyTime prettyTime = new PrettyTime(java.util.Locale.ENGLISH);
    
    public Locale(){
        messages = new HashMap<>();
        prettyTime.removeUnit(JustNow.class);
    }
    
    public Locale(Map<String, String> messages) {
        this();

        this.messages = new HashMap<>(messages);
    }
    
    public Locale(ConfigurationSection config) {
        this();
        
        load(config);
    }
    
    public void load(ConfigurationSection config) {
        messages = new HashMap<>();
        for(String key : config.getKeys(true)) {
            String value = config.getString(key);

            messages.put(key, value);
        }

        String locale = config.getString("locale");
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
        return new MessageBuilder(this);
    }

    /**
     * Puts the given template under the given key
     *
     * @param key the key
     * @param template the template message
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
