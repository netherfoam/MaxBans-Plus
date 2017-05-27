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

import java.time.Duration;

/**
 * @author netherfoam
 */
public class UnbanCommandExecutor extends IPRestrictionCommandExecutor {
    private BroadcastService broadcastService;
    private UserService userService;
    private MetricService metricService;

    public UnbanCommandExecutor(Transactor transactor, Locale locale, LocatorService locatorService, AddressService addressService, BroadcastService broadcastService, UserService userService, MetricService metrics) {
        super(locale, locatorService, "maxbans.ban", addressService, transactor);

        this.broadcastService = broadcastService;
        this.userService = userService;
        this.metricService = metrics;
    }

    @Override
    public void restrict(CommandSender sender, Address address, User user, Duration duration, String reason, boolean silent) throws RejectedException, PermissionException {
        User source = (sender instanceof Player ? userService.getOrCreate((Player) sender) : null);

        MessageBuilder message = locale.get()
                .with("source", source == null ? "Console" : source.getName());

        boolean any = false;
        if(user != null && userService.getBan(user) != null) {
            userService.unban(source, user);
            message.with("name", user.getName());
            broadcastService.broadcast(message.get("ban.unban"), silent, sender);
            any = true;
        }

        if(addressService.getBan(address) != null) {
            addressService.unban(source, address);
            message.with("address", address.getHost());
            broadcastService.broadcast(message.get("ban.unban"), silent, sender);
            any = true;
        }

        if(!any) {
            throw new RejectedException("No ban found");
        }
    }
}
