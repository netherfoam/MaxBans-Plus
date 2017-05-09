package org.maxgamer.maxbans.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.maxgamer.maxbans.exception.MessageException;
import org.maxgamer.maxbans.exception.RejectedException;
import org.maxgamer.maxbans.locale.Locale;
import org.maxgamer.maxbans.orm.User;
import org.maxgamer.maxbans.service.BroadcastService;
import org.maxgamer.maxbans.service.LockdownService;
import org.maxgamer.maxbans.service.UserService;
import org.maxgamer.maxbans.util.Lockdown;
import org.maxgamer.maxbans.util.RestrictionUtil;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * @author netherfoam
 */
public class LockdownCommandExecutor extends StandardCommandExecutor {
    private LockdownService lockdownService;
    private UserService userService;
    private BroadcastService broadcastService;

    public LockdownCommandExecutor(Locale locale, LockdownService lockdownService, UserService userService, BroadcastService broadcastService) {
        super(locale, "maxbans.lockdown");

        this.lockdownService = lockdownService;
        this.userService = userService;
        this.broadcastService = broadcastService;
    }

    @Override
    public void perform(CommandSender sender, Command command, String name, String[] userArgs) throws MessageException {
        User source = (sender instanceof Player ? userService.getOrCreate((Player) sender) : null);
        LinkedList<String> args = new LinkedList<>(Arrays.asList(userArgs));
        boolean silent = RestrictionUtil.isSilent(args);

        if(args.size() <= 0) {
            throw new RejectedException("Must supply a type of lockdown to apply: " + Arrays.toString(Lockdown.values()));
        }

        String type = args.pop();

        Locale.MessageBuilder message = lockdownService.lockdown(source, type, String.join(" ", args), locale);

        // Everyone else gets a message telling them what's happened
        broadcastService.broadcast(message.get("lockdown.broadcast"), silent);
    }
}
