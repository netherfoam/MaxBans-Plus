package org.maxgamer.maxbans.locale;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.maxgamer.maxbans.util.StringUtil;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO: Document this
 */
public class Message {
    protected Locale locale;
    protected Map<String, Object> substitutions;
    protected String templateId;
    protected Message next;

    public Message(Locale locale, Map<String, Object> substitutions, String templateId) {
        this.locale = locale;
        this.substitutions = new HashMap<>(substitutions);
        this.templateId = templateId;
    }

    public Message append(Message suffix) {
        this.next = suffix;

        return this;
    }

    public Message insert(Message prefix) {
        prefix.next = this;

        return prefix;
    }

    protected Object transform(Object value) {
        if (value instanceof Instant) {
            // Instants get turned into dates
            return Date.from((Instant) value);
        }

        if (value instanceof Date) {
            // Instants and Dates get pretty printed
            return locale.prettyTime.format(((Date) value));
        }

        if (value instanceof Duration) {
            // Little bit of a hack because we have a Java 8 Duration object
            // instead of a PrettyTime Duration object. So we add the duration
            // to the current time, and then get pretty time to write the difference
            // between now and then, which hopefully looks like "1 week from now".
            Duration d = (Duration) value;
            Instant duration = Instant.now().plus(d);

            return locale.prettyTime.format(Date.from(duration));
        }

        return value;
    }

    public void send(CommandSender recipient) {
        String output = toString();
        recipient.sendMessage(output);
    }

    @Override
    public String toString() {
        String template = locale.messages.get(this.templateId);
        if (template == null) throw new IllegalArgumentException("No such template: " + this.templateId);

        Map<String, Object> preprocessed = new HashMap<>(substitutions.size());
        for (Map.Entry<String, Object> entry : substitutions.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            value = transform(value);
            preprocessed.put(key, value);
        }

        // Now we expand the template before parsing in variable substitutions
        template = ChatColor.translateAlternateColorCodes('&', template);

        String output = StringUtil.expand(template, preprocessed);
        if (next != null) {
            output += next.toString();
        }

        return output;
    }
}
