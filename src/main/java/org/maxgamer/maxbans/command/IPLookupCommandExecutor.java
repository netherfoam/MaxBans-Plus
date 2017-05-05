package org.maxgamer.maxbans.command;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.maxgamer.maxbans.locale.Locale;
import org.maxgamer.maxbans.orm.User;
import org.maxgamer.maxbans.service.AddressService;
import org.maxgamer.maxbans.service.UserService;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * @author netherfoam
 */
public class IPLookupCommandExecutor extends StandardCommandExecutor {
    private Server server;
    private AddressService addressService;
    private UserService userService;

    public IPLookupCommandExecutor(Locale locale) {
        super(locale, "maxbans.iplookup");
    }

    @Override
    public void perform(CommandSender sender, Command command, String name, String[] userArgs) {
        LinkedList<String> args = new LinkedList<>(Arrays.asList(userArgs));

        User user = userService.get(args.pop());
        if(user == null) {
            sender.sendMessage("Player not found");
            return;
        }

        user.get

    }
}
