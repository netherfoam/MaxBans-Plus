package org.maxgamer.maxbans.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.maxgamer.maxbans.exception.CancelledException;
import org.maxgamer.maxbans.exception.PermissionException;
import org.maxgamer.maxbans.exception.RejectedException;
import org.maxgamer.maxbans.locale.MessageBuilder;
import org.maxgamer.maxbans.orm.Address;
import org.maxgamer.maxbans.orm.User;
import org.maxgamer.maxbans.service.BroadcastService;
import org.maxgamer.maxbans.service.UserService;
import org.maxgamer.maxbans.service.metric.MetricService;
import org.maxgamer.maxbans.util.TemporalDuration;

import javax.inject.Inject;
import java.time.Duration;

/**
 * @author netherfoam
 */
public class IPMuteCommandExecutor extends IPRestrictionCommandExecutor {
    @Inject
    protected UserService userService;

    @Inject
    protected BroadcastService broadcastService;

    @Inject
    protected MetricService metricService;

    @Inject
    public IPMuteCommandExecutor() {
        super("maxbans.ipban");
    }

    @Override
    public void restrict(CommandSender source, Address address, User user, Duration duration, String reason, boolean silent) throws RejectedException, CancelledException {
        User muter = (source instanceof Player ? userService.getOrCreate((Player) source) : null);

        addressService.mute(muter, address, reason, duration);

        MessageBuilder message = locale.get()
                .with("name", user == null ? null : user.getName())
                .with("address", address.getHost())
                .with("reason", reason)
                .with("source", muter == null ? "Console" : muter.getName())
                .with("duration", TemporalDuration.of(duration));

        broadcastService.broadcast(message.get("mute.broadcast"), silent, source, locatorService.player(user));

        metricService.increment(MetricService.IP_MUTES);
    }
}
