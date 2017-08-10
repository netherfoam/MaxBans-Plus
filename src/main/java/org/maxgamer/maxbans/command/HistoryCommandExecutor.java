package org.maxgamer.maxbans.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.maxgamer.maxbans.exception.MessageException;

import javax.inject.Inject;

/**
 * @author netherfoam
 */
public class HistoryCommandExecutor extends StandardCommandExecutor {
    @Inject
    public HistoryCommandExecutor() {
        super("maxbans.history");
    }

    @Override
    public void perform(CommandSender commandSender, Command command, String s, String[] strings) throws MessageException {
        // TODO
    }
}
