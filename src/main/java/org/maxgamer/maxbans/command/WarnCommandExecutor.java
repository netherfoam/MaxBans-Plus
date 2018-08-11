package org.maxgamer.maxbans.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.maxgamer.maxbans.exception.CancelledException;
import org.maxgamer.maxbans.locale.MessageBuilder;
import org.maxgamer.maxbans.orm.User;
import org.maxgamer.maxbans.service.BroadcastService;
import org.maxgamer.maxbans.service.UserService;
import org.maxgamer.maxbans.service.WarningService;
import org.maxgamer.maxbans.service.metric.MetricService;

import javax.inject.Inject;
import java.time.Duration;

/**
 * @author netherfoam
 */
public class WarnCommandExecutor extends UserRestrictionCommandExecutor {
    @Inject
    protected UserService userService;

    @Inject
    protected WarningService warningService;

    @Inject
    protected BroadcastService broadcastService;

    @Inject
    protected MetricService metricService;

    @Inject
    public WarnCommandExecutor() {
        super("maxbans.warn");
    }

    @Override
    public void restrict(CommandSender source, User user, Duration duration, String reason, boolean silent) throws CancelledException {
        User banner = (source instanceof Player ? userService.getOrCreate((Player) source) : null);

        MessageBuilder message = warningService.warn(banner, user, reason, locale);
        broadcastService.broadcast(message.get("warn.broadcast"), silent, source, locatorService.player(user));
        metricService.increment(MetricService.WARNINGS);
    }
}
