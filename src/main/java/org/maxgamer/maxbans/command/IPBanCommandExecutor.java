package org.maxgamer.maxbans.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.maxgamer.maxbans.exception.PermissionException;
import org.maxgamer.maxbans.exception.RejectedException;
import org.maxgamer.maxbans.locale.Locale;
import org.maxgamer.maxbans.locale.MessageBuilder;
import org.maxgamer.maxbans.orm.Address;
import org.maxgamer.maxbans.orm.User;
import org.maxgamer.maxbans.service.*;
import org.maxgamer.maxbans.transaction.Transactor;
import org.maxgamer.maxbans.util.TemporalDuration;

import java.time.Duration;

/**
 * @author netherfoam
 */
public class IPBanCommandExecutor extends IPRestrictionCommandExecutor {
    private UserService userService;
    private BroadcastService broadcastService;
    private MetricService metricService;

    public IPBanCommandExecutor(Locale locale, LocatorService locatorService, Transactor transactor, AddressService addressService, UserService userService, BroadcastService broadcastService, MetricService metrics) {
        super(locale, locatorService, "maxbans.ipban", addressService, transactor);
        this.userService = userService;
        this.broadcastService = broadcastService;
        this.metricService = metrics;
    }

    @Override
    public void restrict(CommandSender source, Address address, User user, Duration duration, String reason, boolean silent) throws RejectedException, PermissionException {
        User banner = (source instanceof Player ? userService.getOrCreate((Player) source) : null);

        addressService.ban(banner, address, reason, duration);

        MessageBuilder message = locale.get()
                .with("name", user == null ? null : user.getName())
                .with("address", address.getHost())
                .with("reason", reason)
                .with("source", banner == null ? "Console" : banner.getName())
                .with("duration", TemporalDuration.of(duration));

        broadcastService.broadcast(message.get("ban.broadcast"), silent, source);

        for(Player player : locatorService.players(address)) {
            player.kickPlayer(message.get("ban.kick"));
        }

        // Shouldn't be necessary, if everything else is working, to kick the player by retrieving them by the user object here.
        metricService.increment(MetricService.IP_BANS);
    }
}
