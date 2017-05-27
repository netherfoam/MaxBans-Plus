package org.maxgamer.maxbans.exception;

import org.bukkit.command.CommandSender;
import org.maxgamer.maxbans.locale.Locale;
import org.maxgamer.maxbans.locale.MessageBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * @author netherfoam
 */
public class MessageException extends Exception {
    private Map<String, Object> substitutions = new HashMap<>(6);

    public MessageException(String message) {
        super(message);
    }

    public MessageException with(String key, Object value) {
        substitutions.put(key, value);

        return this;
    }

    public void send(Locale locale, CommandSender sender) {
        sender.sendMessage(getMessage(locale));
    }

    public String getMessage(Locale locale) {
        if(locale.has(getMessage())) {
            // The message has a translation
            MessageBuilder builder = toBuilder(locale);

           return builder.get(getMessage());
        } else {
            // This message has no translation. Perhaps it's not defined or perhaps it's a lazy message
            return super.getMessage();
        }
    }

    public MessageBuilder toBuilder(Locale locale) {
        MessageBuilder builder = locale.get();
        for (Map.Entry<String, Object> entry : substitutions.entrySet()) {
            builder.with(entry.getKey(), entry.getValue());
        }

        return builder;
    }
}
