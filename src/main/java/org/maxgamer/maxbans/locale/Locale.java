package org.maxgamer.maxbans.locale;

import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

            String result = "";
            Matcher matcher = Pattern.compile("\\{\\{[^\\}\\}]*\\}\\}").matcher(template);
            
            int last = 0;
            while(matcher.find()) {
                int start = matcher.start();
                int end = matcher.end();
                
                String group = matcher.group();
                result += template.substring(last, start);
                last = end;
                
                int defaultSeparator = group.lastIndexOf('|');
                String identifier;
                String defaultValue;
                if(defaultSeparator >= 0) {
                    identifier = group.substring(2, defaultSeparator);
                    defaultValue = group.substring(defaultSeparator + 1, group.length() - 2);
                } else {
                    identifier = group.substring(2, group.length() - 2);
                    defaultValue = "MISSING";
                }
                
                Object value = substitutions.get(identifier);
                if(value == null) value = defaultValue;
                
                result += value;
            }
            result += template.substring(last);
            
            return result;
        }
    }
    
    private HashMap<String, String> messages;
    
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
    }
    
    public MessageBuilder get() {
        return new MessageBuilder();
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
