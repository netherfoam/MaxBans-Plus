package org.maxgamer.maxbans.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.maxgamer.maxbans.transaction.Transactor;

/**
 * @author netherfoam
 */
public abstract class TransactionalCommandExecutor implements CommandExecutor {
    protected final Transactor transactor;

    public TransactionalCommandExecutor(Transactor transactor) {
        this.transactor = transactor;
    }

    @Override
    public final boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        transactor.work(session -> transact(commandSender, command, s, strings));

        return true;
    }

    public abstract void transact(CommandSender sender, Command command, String commandName, String[] args);
}
