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
public class IPBanCommandExecutor extends IPRestrictionCommandExecutor {
    @Inject
    protected UserService userService;

    @Inject
    protected BroadcastService broadcastService;

    @Inject
    protected MetricService metricService;

    @Inject
    public IPBanCommandExecutor() {
        super("maxbans.ipban");
    }

    @Override
    public void restrict(CommandSender source, Address address, User user, Duration duration, String reason, boolean silent) throws RejectedException, PermissionException, CancelledException {
        User banner = (source instanceof Player ? userService.getOrCreate((Player) source) : null);

        addressService.ban(banner, address, reason, duration);

        MessageBuilder message = locale.get()
                .with("name", user)
                .with("address", address.getHost())
                .with("reason", reason)
                .withUserOrConsole("source", banner)
                .with("duration", TemporalDuration.of(duration));

        broadcastService.broadcast(message.get("ipban.broadcast"), silent, source);

        for(Player player : locatorService.players(address)) {
            player.kickPlayer(message.get("ipban.kick"));
        }

        // Shouldn't be necessary, if everything else is working, to kick the player by retrieving them by the user object here.
        metricService.increment(MetricService.IP_BANS);
    }
}
