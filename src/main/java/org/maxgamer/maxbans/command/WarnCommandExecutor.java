package org.maxgamer.maxbans.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.maxgamer.maxbans.locale.Locale;
import org.maxgamer.maxbans.locale.MessageBuilder;
import org.maxgamer.maxbans.orm.User;
import org.maxgamer.maxbans.service.*;
import org.maxgamer.maxbans.transaction.Transactor;

import java.time.Duration;

/**
 * @author netherfoam
 */
public class WarnCommandExecutor extends UserRestrictionCommandExecutor {
    private UserService userService;
    private WarningService warningService;
    private BroadcastService broadcastService;
    private MetricService metricService;

    public WarnCommandExecutor(Locale locale, Transactor transactor, LocatorService locatorService, UserService userService, WarningService warningService, BroadcastService broadcastService, MetricService metrics) {
        super(locale, locatorService, "maxbans.warn", transactor);

        this.userService = userService;
        this.warningService = warningService;
        this.broadcastService = broadcastService;
        this.metricService = metrics;
    }

    @Override
    public void restrict(CommandSender source, User user, Duration duration, String reason, boolean silent) {
        User banner = (source instanceof Player ? userService.getOrCreate((Player) source) : null);

        MessageBuilder message = warningService.warn(banner, user, reason, locale);
        broadcastService.broadcast(message.get("warn.broadcast"), silent, source, locatorService.player(user));
        metricService.increment(MetricService.WARNINGS);
    }
}
