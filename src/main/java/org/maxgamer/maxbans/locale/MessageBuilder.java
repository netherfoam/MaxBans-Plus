package org.maxgamer.maxbans.locale;

import org.bukkit.command.CommandSender;
import org.maxgamer.maxbans.orm.User;

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

    public Message get(String templateId) {
        return new Message(locale, substitutions, templateId);
    }

    public void send(String templateId, CommandSender recipient) {
        get(templateId).send(recipient);
    }
}
