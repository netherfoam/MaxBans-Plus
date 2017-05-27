package org.maxgamer.maxbans.locale;

import org.bukkit.ChatColor;
import org.maxgamer.maxbans.util.StringUtil;

import java.util.Date;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * @author netherfoam
 */
public class MessageBuilder {
    private Locale locale;
    private Map<String, Object> substitutions = new HashMap<>(6);

    public MessageBuilder(Locale locale) {
        this.locale = locale;
    }

    public MessageBuilder with(String key, Object value) {
        substitutions.put(key, value);

        return this;
    }

    public Object preview(String key) {
        return substitutions.get(key);
    }

    public String get(String name) {
        String template = locale.messages.get(name);
        if (template == null) throw new IllegalArgumentException("No such template: " + name);

        Map<String, Object> preprocessed = new HashMap<>(substitutions.size());
        for (Map.Entry<String, Object> entry : substitutions.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Instant) {
                // Instants get turned into dates
                value = Date.from((Instant) value);
            }

            if (value instanceof Date) {
                // Instants and Dates get pretty printed
                value = locale.prettyTime.format(((Date) value));
            }

            preprocessed.put(key, value);
        }

        // Now we expand the template before parsing in variable substitutions
        template = ChatColor.translateAlternateColorCodes('&', template);

        return StringUtil.expand(template, preprocessed);
    }
}
