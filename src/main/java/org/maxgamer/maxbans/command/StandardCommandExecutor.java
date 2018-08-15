package org.maxgamer.maxbans.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.maxgamer.maxbans.exception.CancelledException;
import org.maxgamer.maxbans.exception.MessageException;
import org.maxgamer.maxbans.exception.PermissionException;
import org.maxgamer.maxbans.locale.Locale;

import javax.inject.Inject;

/**
 * @author netherfoam
 */
public abstract class StandardCommandExecutor extends TransactionalCommandExecutor {
    @Inject
    protected Locale locale;

    protected String permission;

    public StandardCommandExecutor(String permission) {
        this.permission = permission;
    }

    protected void permiss(CommandSender sender) throws PermissionException {
        if(!sender.hasPermission(permission)) {
            throw new PermissionException("permission.required").with("permission", permission);
        }
    }

    @Override
    public final void transact(CommandSender sender, Command command, String name, String[] args) {
        try {
            permiss(sender);
            perform(sender, command, name, args);
        } catch (PermissionException e) {
            e.with("permission", permission).send(locale, sender);
        } catch (MessageException e) {
            e.send(locale, sender);
        } catch (CancelledException e) {
            // We assume there was feedback or a message sent to the player
        }
    }

    public abstract void perform(CommandSender commandSender, Command command, String s, String[] strings) throws MessageException, CancelledException;
}
