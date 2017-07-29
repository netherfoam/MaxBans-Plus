package org.maxgamer.maxbans.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.maxgamer.maxbans.exception.MessageException;
import org.maxgamer.maxbans.locale.Locale;
import org.maxgamer.maxbans.transaction.Transactor;

import java.util.logging.Logger;

/**
 * @author netherfoam
 */
public class HistoryCommandExecutor extends StandardCommandExecutor {
    public HistoryCommandExecutor(Transactor transactor, Locale locale, Logger logger, String permission) {
        super(transactor, locale, logger, permission);
    }

    @Override
    public void perform(CommandSender commandSender, Command command, String s, String[] strings) throws MessageException {
        // TODO
    }
}
