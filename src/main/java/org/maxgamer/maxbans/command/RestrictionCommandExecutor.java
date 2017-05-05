package org.maxgamer.maxbans.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.maxgamer.maxbans.exception.PermissionException;
import org.maxgamer.maxbans.exception.RejectedException;
import org.maxgamer.maxbans.locale.Locale;
import org.maxgamer.maxbans.orm.User;
import org.maxgamer.maxbans.service.LocatorService;
import org.maxgamer.maxbans.util.RestrictionUtil;

import java.time.Duration;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * @author Dirk Jamieson <dirk@redeye.co>
 */
public abstract class RestrictionCommandExecutor extends StandardCommandExecutor {
    protected final LocatorService locatorService;

    public RestrictionCommandExecutor(Locale locale, LocatorService locatorService, String permission) {
        super(locale, permission);
        this.locatorService = locatorService;
    }

    @Override
    public final void perform(CommandSender sender, Command command, String s, String[] userArgs) throws RejectedException, PermissionException {
        LinkedList<String> args = new LinkedList<>(Arrays.asList(userArgs));

        if(args.size() <= 0) {
            sender.sendMessage("Must supply target name");
            return;
        }

        User user = locatorService.user(args.pop());
        if(user == null) {
            sender.sendMessage("Player not found");
            return;
        }

        Duration duration = null;
        String reason = null;

        if(args.size() >= 2) {
            duration = RestrictionUtil.getDuration(args);
            reason = String.join(" ", args);
        }

        restrict(sender, user, duration, reason);
    }
    
    public abstract void restrict(CommandSender source, User user, Duration duration, String reason) throws RejectedException, PermissionException;
}
