package org.maxgamer.maxbans.command;

import org.bukkit.command.CommandSender;
import org.maxgamer.maxbans.exception.PermissionException;
import org.maxgamer.maxbans.exception.RejectedException;
import org.maxgamer.maxbans.locale.Locale;
import org.maxgamer.maxbans.orm.Address;
import org.maxgamer.maxbans.orm.User;
import org.maxgamer.maxbans.service.AddressService;
import org.maxgamer.maxbans.service.LocatorService;
import org.maxgamer.maxbans.transaction.Transactor;

import java.time.Duration;
import java.util.logging.Logger;

/**
 * @author netherfoam
 */
public class LookupCommandExecutor extends IPRestrictionCommandExecutor {
    public LookupCommandExecutor(Transactor transactor, Locale locale, Logger logger, LocatorService locatorService, AddressService addressService) {
        super(locale, logger, locatorService, "maxbans.iplookup", addressService, transactor);
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
