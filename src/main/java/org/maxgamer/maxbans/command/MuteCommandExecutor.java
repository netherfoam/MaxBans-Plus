package org.maxgamer.maxbans.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.maxgamer.maxbans.exception.RejectedException;
import org.maxgamer.maxbans.locale.Locale;
import org.maxgamer.maxbans.orm.User;
import org.maxgamer.maxbans.service.BroadcastService;
import org.maxgamer.maxbans.service.LocatorService;
import org.maxgamer.maxbans.service.MetricService;
import org.maxgamer.maxbans.service.UserService;
import org.maxgamer.maxbans.transaction.Transactor;
import org.maxgamer.maxbans.util.TemporalDuration;

import java.time.Duration;

/**
 * @author Dirk Jamieson <dirk@redeye.co>
 */
public class MuteCommandExecutor extends UserRestrictionCommandExecutor {
    private BroadcastService broadcastService;
    private UserService userService;
    private MetricService metricService;

    public MuteCommandExecutor(Transactor transactor, LocatorService locatorService, UserService userService, BroadcastService broadcastService, Locale locale, MetricService metrics) {
        super(locale, locatorService, "maxbans.mute", transactor);
        this.userService = userService;
        this.broadcastService = broadcastService;
        this.metricService = metrics;
    }

    @Override
    public void restrict(CommandSender source, User user, Duration duration, String reason, boolean silent) throws RejectedException {
        User banner = (source instanceof Player ? userService.getOrCreate((Player) source) : null);
        
        userService.mute(banner, user, reason, duration);
        
        Locale.MessageBuilder message = locale.get()
                .with("name", user.getName())
                .with("reason", reason)
                .with("source", banner == null ? "Console" : banner.getName())
                .with("duration", TemporalDuration.of(duration));
        
        broadcastService.broadcast(message.get("mute.broadcast"), silent, source, locatorService.player(user));
        metricService.increment(MetricService.USER_MUTES);
    }
}
