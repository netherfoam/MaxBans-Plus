package org.maxgamer.maxbans.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.maxgamer.maxbans.exception.PermissionException;
import org.maxgamer.maxbans.exception.RejectedException;
import org.maxgamer.maxbans.locale.MessageBuilder;
import org.maxgamer.maxbans.orm.Address;
import org.maxgamer.maxbans.orm.User;
import org.maxgamer.maxbans.service.AddressService;
import org.maxgamer.maxbans.service.BroadcastService;
import org.maxgamer.maxbans.service.UserService;
import org.maxgamer.maxbans.service.metric.MetricService;

import javax.inject.Inject;
import java.time.Duration;

/**
 * @author netherfoam
 */
public class UnmuteCommandExecutor extends IPRestrictionCommandExecutor {
    @Inject
    private BroadcastService broadcastService;

    @Inject
    private AddressService addressService;

    @Inject
    private UserService userService;

    @Inject
    private MetricService metricService;

    @Inject
    public UnmuteCommandExecutor() {
        super("maxbans.mute");
    }

    @Override
    public void restrict(CommandSender sender, Address address, User user, Duration duration, String reason, boolean silent) throws RejectedException, PermissionException {
        User source = (sender instanceof Player ? userService.getOrCreate((Player) sender) : null);

        MessageBuilder message = locale.get()
                .with("source", source == null ? "Console" : source.getName());

        boolean any = false;
        if(user != null && userService.getMute(user) != null) {
            userService.unmute(source, user);
            message.with("name", user.getName());
            any = true;
        }

        if(addressService.getMute(address) != null) {
            addressService.unmute(source, address);
            message.with("address", address.getHost());
            any = true;
        }

        if(!any) {
            throw new RejectedException("No mute found");
        }

        broadcastService.broadcast(message.get("mute.unmute"), silent, sender);
    }
}
