package org.maxgamer.maxbans.locale;

import org.bukkit.command.CommandSender;
import org.maxgamer.maxbans.orm.User;

import java.util.HashMap;
import java.util.Map;

/**
 * @author netherfoam
 */
public interface MessageBuilder {
    MessageBuilder with(String key, Object value);
    MessageBuilder withUserOrConsole(String key, User user);
    MessageBuilder with(String key, User user);
    Object preview(String key);
    Message get(String templateId);
    void send(String templateId, CommandSender recipient);
}
