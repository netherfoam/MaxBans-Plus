package org.maxgamer.maxbans.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.maxgamer.maxbans.exception.CancelledException;
import org.maxgamer.maxbans.exception.RejectedException;
import org.maxgamer.maxbans.locale.MessageBuilder;
import org.maxgamer.maxbans.orm.User;
import org.maxgamer.maxbans.service.BroadcastService;
import org.maxgamer.maxbans.service.UserService;
import org.maxgamer.maxbans.service.metric.MetricService;
import org.maxgamer.maxbans.util.TemporalDuration;

import javax.inject.Inject;
import java.time.Duration;

/**
 * @author Dirk Jamieson <dirk@redeye.co>
 */
public class MuteCommandExecutor extends UserRestrictionCommandExecutor {
    @Inject
    protected BroadcastService broadcastService;

    @Inject
    protected UserService userService;

    @Inject
    protected MetricService metricService;

    @Inject
    public MuteCommandExecutor() {
        super("maxbans.mute");
    }

    @Override
    public void restrict(CommandSender source, User user, Duration duration, String reason, boolean silent) throws RejectedException, CancelledException {
        User banner = (source instanceof Player ? userService.getOrCreate((Player) source) : null);
        
        userService.mute(banner, user, reason, duration);
        
        MessageBuilder message = locale.get()
                .with("name", user)
                .with("reason", reason)
                .withUserOrConsole("source", banner)
                .with("duration", TemporalDuration.of(duration));
        
        broadcastService.broadcast(message.get("mute.broadcast"), silent, source, locatorService.player(user));
        metricService.increment(MetricService.USER_MUTES);
    }
}
