package org.maxgamer.maxbans.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.maxgamer.maxbans.exception.PermissionException;
import org.maxgamer.maxbans.exception.RejectedException;
import org.maxgamer.maxbans.locale.Locale;
import org.maxgamer.maxbans.orm.User;
import org.maxgamer.maxbans.service.BroadcastService;
import org.maxgamer.maxbans.service.LocatorService;
import org.maxgamer.maxbans.service.UserService;
import org.maxgamer.maxbans.transaction.Transactor;

import java.time.Duration;

/**
 * @author netherfoam
 */
public class UnbanCommandExecutor extends RestrictionCommandExecutor {
    private BroadcastService broadcastService;
    private UserService userService;

    public UnbanCommandExecutor(Transactor transactor, Locale locale, LocatorService locatorService, BroadcastService broadcastService, UserService userService) {
        super(locale, locatorService, "maxbans.mute", transactor);

        this.broadcastService = broadcastService;
        this.userService = userService;
    }

    @Override
    public void restrict(CommandSender sender, User user, Duration duration, String reason, boolean silent) throws RejectedException, PermissionException {
        User source = (sender instanceof Player ? userService.getOrCreate((Player) sender) : null);

        userService.unban(source, user);

        Locale.MessageBuilder message = locale.get()
                .with("source", source == null ? "Console" : source.getName())
                .with("name", user.getName());

        broadcastService.broadcast(message.get("ban.unban"), silent);
    }
}
