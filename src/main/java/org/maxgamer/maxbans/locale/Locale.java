package org.maxgamer.maxbans.locale;

import org.bukkit.configuration.ConfigurationSection;
import org.maxgamer.maxbans.util.StringUtil;
import org.ocpsoft.prettytime.PrettyTime;

import java.sql.Date;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Dirk Jamieson <dirk@redeye.co>
 */
public class Locale {
    public class MessageBuilder {
        private Map<String, Object> substitutions = new HashMap<>(6);

        public MessageBuilder with(String key, Object value) {
            substitutions.put(key, value);
            
            return this;
        }

        public Object preview(String key) {
            return substitutions.get(key);
        }
        
        public String get(String name) {
            String template = messages.get(name);
            if(template == null) throw new IllegalArgumentException("No such template: " + name);

            Map<String, Object> preprocessed = new HashMap<>(substitutions.size());
            for(Map.Entry<String, Object> entry : substitutions.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                if(value instanceof Instant) {
                    // Instants get turned into dates
                    value = Date.from((Instant) value);
                }

                if(value instanceof Date) {
                    // Instants and Dates get pretty printed
                    value = prettyTime.format(((Date) value));
                }

                preprocessed.put(key, value);
            }

            return StringUtil.expand(template, substitutions);
        }
    }
    
    private HashMap<String, String> messages;
    private PrettyTime prettyTime = new PrettyTime(java.util.Locale.ENGLISH);
    
    public Locale(){
        messages = new HashMap<>();
    }
    
    public Locale(Map<String, String> messages) {
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
    
    public MessageBuilder get() {
        return new MessageBuilder();
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
