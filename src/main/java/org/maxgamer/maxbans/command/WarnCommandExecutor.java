package org.maxgamer.maxbans.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.maxgamer.maxbans.locale.Locale;
import org.maxgamer.maxbans.orm.User;
import org.maxgamer.maxbans.service.BroadcastService;
import org.maxgamer.maxbans.service.LocatorService;
import org.maxgamer.maxbans.service.UserService;
import org.maxgamer.maxbans.service.WarningService;

import java.time.Duration;

/**
 * @author netherfoam
 */
public class WarnCommandExecutor extends RestrictionCommandExecutor {
    private UserService userService;
    private WarningService warningService;
    private BroadcastService broadcastService;

    public WarnCommandExecutor(Locale locale, LocatorService locatorService, UserService userService, WarningService warningService, BroadcastService broadcastService) {
        super(locale, locatorService, "maxbans.warn");

        this.userService = userService;
        this.warningService = warningService;
        this.broadcastService = broadcastService;
    }

    @Override
    public void restrict(CommandSender source, User user, Duration duration, String reason, boolean silent) {
        User banner = (source instanceof Player ? userService.getOrCreate((Player) source) : null);

        Locale.MessageBuilder message = warningService.warn(banner, user, reason, locale);
        broadcastService.broadcast(message.get("warn.broadcast"), silent);
    }
}
