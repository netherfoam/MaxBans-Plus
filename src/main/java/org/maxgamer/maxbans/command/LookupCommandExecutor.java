package org.maxgamer.maxbans.command;

import org.bukkit.command.CommandSender;
import org.maxgamer.maxbans.exception.PermissionException;
import org.maxgamer.maxbans.exception.RejectedException;
import org.maxgamer.maxbans.orm.Address;
import org.maxgamer.maxbans.orm.User;

import javax.inject.Inject;
import java.time.Duration;

/**
 * @author netherfoam
 */
public class LookupCommandExecutor extends IPRestrictionCommandExecutor {
    @Inject
    public LookupCommandExecutor() {
        super("maxbans.iplookup");
    }

    @Override
    public void restrict(CommandSender source, Address address, User user, Duration duration, String reason, boolean silent) throws RejectedException, PermissionException {
        if(user != null) {
            String message = addressService
                    .report(user, locale)
                    .get("iplookup.user");

            source.sendMessage(message);
            return;
        }

        // Address can't be null
        String message = addressService
                .report(address, locale)
                .get("iplookup.ip");

        source.sendMessage(message);
    }
}
