package org.maxgamer.maxbans.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.maxgamer.maxbans.locale.Locale;
import org.maxgamer.maxbans.orm.User;
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

    public WarnCommandExecutor(Locale locale, LocatorService locatorService, UserService userService, WarningService warningService) {
        super(locale, locatorService, "maxbans.warn");

        this.userService = userService;
        this.warningService = warningService;
    }

    @Override
    public void restrict(CommandSender source, User user, Duration duration, String reason, boolean silent) {
        User banner = (source instanceof Player ? userService.getOrCreate((Player) source) : null);

        // TODO: Silence.. and maybe this can be handled better?
        warningService.warn(banner, user, reason, locale);
    }
}
