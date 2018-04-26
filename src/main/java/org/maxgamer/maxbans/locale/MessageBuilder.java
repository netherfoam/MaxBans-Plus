package org.maxgamer.maxbans.locale;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.maxgamer.maxbans.orm.User;
import org.maxgamer.maxbans.util.StringUtil;

import java.time.Duration;
import java.util.Date;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * @author netherfoam
 */
public class MessageBuilder {
    protected Locale locale;
    protected Map<String, Object> substitutions = new HashMap<>(6);

    public MessageBuilder(Locale locale) {
        this.locale = locale;
    }

    public MessageBuilder with(String key, Object value) {
        substitutions.put(key, value);

        return this;
    }

    public MessageBuilder withUserOrConsole(String key, User user) {
        return with(key, user == null ? "Console" : user.getName());
    }

    public MessageBuilder with(String key, User user) {
        if (user == null) return this;

        return with(key, user.getName());
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

            if (value instanceof Duration) {
                // Little bit of a hack because we have a Java 8 Duration object
                // instead of a PrettyTime Duration object. So we add the duration
                // to the current time, and then get pretty time to write the difference
                // between now and then, which hopefully looks like "1 week from now".
                Duration d = (Duration) value;
                Instant duration = Instant.now().plus(d);
                value = locale.prettyTime.format(Date.from(duration));
            }

            preprocessed.put(key, value);
        }

        // Now we expand the template before parsing in variable substitutions
        template = ChatColor.translateAlternateColorCodes('&', template);

        return StringUtil.expand(template, preprocessed);
    }
}
