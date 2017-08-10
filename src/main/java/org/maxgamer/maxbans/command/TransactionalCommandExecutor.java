package org.maxgamer.maxbans.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.maxgamer.maxbans.transaction.Transactor;

import javax.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author netherfoam
 */
public abstract class TransactionalCommandExecutor implements CommandExecutor {
    @Inject
    protected Transactor transactor;

    @Inject
    protected Logger logger;

    @Override
    public final boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        try {
            transactor.work(session -> transact(commandSender, command, s, strings));
            return true;
        } catch (Throwable t) {
            // Log our exception to Sentry so it can be fixed
            logger.log(Level.WARNING, "Failed to execute " + command.getLabel(), t);

            // Rethrow our exception
            throw t;
        }
    }

    public abstract void transact(CommandSender sender, Command command, String commandName, String[] args);
}
