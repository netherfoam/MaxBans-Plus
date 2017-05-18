package org.maxgamer.maxbans.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.maxgamer.maxbans.exception.PermissionException;
import org.maxgamer.maxbans.exception.RejectedException;
import org.maxgamer.maxbans.locale.Locale;
import org.maxgamer.maxbans.orm.Address;
import org.maxgamer.maxbans.orm.User;
import org.maxgamer.maxbans.service.AddressService;
import org.maxgamer.maxbans.service.BroadcastService;
import org.maxgamer.maxbans.service.LocatorService;
import org.maxgamer.maxbans.service.UserService;
import org.maxgamer.maxbans.transaction.Transactor;
import org.maxgamer.maxbans.util.TemporalDuration;

import java.time.Duration;

/**
 * @author netherfoam
 */
public class IPMuteCommandExecutor extends IPRestrictionCommandExecutor {
    private UserService userService;
    private BroadcastService broadcastService;

    public IPMuteCommandExecutor(Locale locale, LocatorService locatorService, Transactor transactor, AddressService addressService, UserService userService, BroadcastService broadcastService) {
        super(locale, locatorService, "maxbans.ipban", addressService, transactor);
        this.userService = userService;
        this.broadcastService = broadcastService;
    }

    @Override
    public void restrict(CommandSender source, Address address, User user, Duration duration, String reason, boolean silent) throws RejectedException, PermissionException {
        User muter = (source instanceof Player ? userService.getOrCreate((Player) source) : null);

        addressService.mute(muter, address, reason, duration);

        Locale.MessageBuilder message = locale.get()
                .with("name", user == null ? null : user.getName())
                .with("address", address.getHost())
                .with("reason", reason)
                .with("source", muter == null ? "Console" : muter.getName())
                .with("duration", TemporalDuration.of(duration));

        broadcastService.broadcast(message.get("ipmute.broadcast"), silent);
    }
}
