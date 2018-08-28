package org.maxgamer.maxbans.locale;

import org.bukkit.command.CommandSender;

/**
 * TODO: Document this
 */
public interface Message<T extends Message> {
    T append(T suffix);
    T insert(T prefix);
    void send(CommandSender recipient);
}
