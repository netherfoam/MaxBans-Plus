package org.maxgamer.maxbans.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.maxgamer.maxbans.exception.RejectedException;
import org.maxgamer.maxbans.locale.Locale;
import org.maxgamer.maxbans.orm.User;
import org.maxgamer.maxbans.service.AddressService;
import org.maxgamer.maxbans.service.LocatorService;
import org.maxgamer.maxbans.transaction.Transactor;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * @author netherfoam
 */
public class IPLookupCommandExecutor extends StandardCommandExecutor {
    private AddressService addressService;
    private LocatorService locatorService;

    public IPLookupCommandExecutor(Transactor transactor, Locale locale, LocatorService locatorService, AddressService addressService) {
        super(transactor, locale, "maxbans.iplookup");

        this.locatorService = locatorService;
        this.addressService = addressService;
    }

    @Override
    public void perform(CommandSender sender, Command command, String name, String[] userArgs) throws RejectedException {
        LinkedList<String> args = new LinkedList<>(Arrays.asList(userArgs));

        User user = locatorService.user(args.pop());
        if(user == null) {
            sender.sendMessage("Player not found");
            return;
        }

        String message = addressService
                .report(user, locale)
                .get("iplookup.format");

        sender.sendMessage(message);
    }
}
