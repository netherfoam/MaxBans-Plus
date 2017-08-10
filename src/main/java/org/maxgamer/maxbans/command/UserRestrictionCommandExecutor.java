package org.maxgamer.maxbans.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.maxgamer.maxbans.exception.MessageException;
import org.maxgamer.maxbans.exception.PermissionException;
import org.maxgamer.maxbans.exception.RejectedException;
import org.maxgamer.maxbans.exception.TransactionException;
import org.maxgamer.maxbans.orm.User;
import org.maxgamer.maxbans.service.LocatorService;
import org.maxgamer.maxbans.util.RestrictionUtil;

import javax.inject.Inject;
import java.time.Duration;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * @author Dirk Jamieson <dirk@redeye.co>
 */
public abstract class UserRestrictionCommandExecutor extends StandardCommandExecutor {
    @Inject
    protected LocatorService locatorService;

    public UserRestrictionCommandExecutor(String permission) {
        super(permission);
    }

    @Override
    public final void perform(CommandSender sender, Command command, String s, String[] userArgs) throws MessageException {
        LinkedList<String> args = new LinkedList<>(Arrays.asList(userArgs));
        boolean silent = RestrictionUtil.isSilent(args);

        if(args.size() <= 0) {
            sender.sendMessage("Must supply target name");
            return;
        }

        try {
            transactor.work(session -> {
                User user = locatorService.user(args.pop());
                if (user == null) {
                    sender.sendMessage("Player not found");
                    return;
                }

                Duration duration = RestrictionUtil.getDuration(args);
                String reason = String.join(" ", args);

                restrict(sender, user, duration, reason, silent);
            });
        } catch (TransactionException e) {
            if(e.getCause() instanceof MessageException) {
                throw (MessageException) e.getCause();
            }

            throw e;
        }
    }
    
    public abstract void restrict(CommandSender source, User user, Duration duration, String reason, boolean silent) throws RejectedException, PermissionException;
}
