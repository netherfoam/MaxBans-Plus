package org.maxgamer.maxbans.command.executor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.maxgamer.maxbans.exception.MessageException;
import org.maxgamer.maxbans.exception.PermissionException;
import org.maxgamer.maxbans.locale.Locale;

/**
 * @author netherfoam
 */
public abstract class StandardCommandExecutor implements CommandExecutor {
    protected final Locale locale;
    protected final String permission;

    public StandardCommandExecutor(Locale locale, String permission) {
        this.locale = locale;
        this.permission = permission;
    }

    protected void permiss(CommandSender sender) throws PermissionException {
        if(!sender.hasPermission(permission)) {
            throw new PermissionException("permission.required").with("permission", permission);
        }
    }

    @Override
    public final boolean onCommand(CommandSender sender, Command command, String name, String[] args) {
        try {
            permiss(sender);
            perform(sender, command, name, args);
        } catch (PermissionException e) {
            e.with("permission", permission).send(locale, sender);
        } catch (MessageException e) {
            e.send(locale, sender);
        }

        return true;
    }

    public abstract void perform(CommandSender commandSender, Command command, String s, String[] strings) throws MessageException;
}
