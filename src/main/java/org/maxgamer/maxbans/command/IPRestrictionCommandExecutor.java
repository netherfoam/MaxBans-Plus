package org.maxgamer.maxbans.command;

import com.google.common.net.InetAddresses;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.maxgamer.maxbans.exception.MessageException;
import org.maxgamer.maxbans.exception.PermissionException;
import org.maxgamer.maxbans.exception.RejectedException;
import org.maxgamer.maxbans.exception.TransactionException;
import org.maxgamer.maxbans.locale.Locale;
import org.maxgamer.maxbans.orm.Address;
import org.maxgamer.maxbans.orm.User;
import org.maxgamer.maxbans.service.AddressService;
import org.maxgamer.maxbans.service.LocatorService;
import org.maxgamer.maxbans.transaction.Transactor;
import org.maxgamer.maxbans.util.RestrictionUtil;

import java.time.Duration;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * @author Dirk Jamieson <dirk@redeye.co>
 */
public abstract class IPRestrictionCommandExecutor extends StandardCommandExecutor {
    protected final AddressService addressService;
    protected final LocatorService locatorService;
    protected final Transactor transactor;

    public IPRestrictionCommandExecutor(Locale locale, LocatorService locatorService, String permission, AddressService addressService, Transactor transactor) {
        super(transactor, locale, permission);
        this.locatorService = locatorService;
        this.addressService = addressService;
        this.transactor = transactor;
    }

    @Override
    public final void perform(CommandSender sender, Command command, String s, String[] userArgs) throws MessageException {
        LinkedList<String> args = new LinkedList<>(Arrays.asList(userArgs));
        boolean silent = RestrictionUtil.isSilent(args);

        if(args.size() <= 0) {
            sender.sendMessage("Must supply target IP or user");
            return;
        }

        try {
            transactor.work(session -> {
                String ipOrUser = args.pop();
                User user = null;
                String ip;

                try {
                    ip = InetAddresses.forString(ipOrUser).getHostAddress();
                } catch (IllegalArgumentException e) {
                    user = locatorService.user(ipOrUser);

                    if(user.getAddresses().isEmpty()) {
                        sender.sendMessage("Player has no IP history");
                        return;
                    }

                    ip = user.getAddresses().get(user.getAddresses().size() - 1).getAddress().getHost();
                }

                Duration duration = RestrictionUtil.getDuration(args);
                String reason = String.join(" ", args);
                Address address = addressService.getOrCreate(ip);

                restrict(sender, address, user, duration, reason, silent);
            });
        } catch (TransactionException e) {
            if(e.getCause() instanceof MessageException) {
                throw (MessageException) e.getCause();
            }

            throw e;
        }
    }

    /**
     * Runs this command
     * @param source the command sender
     * @param address the targeted IP address
     * @param user the targeted user, may be null if an ip was explicitly targeted
     * @param duration the duration of the penalty
     * @param reason the reason
     * @param silent true if the command is to be silent
     * @throws RejectedException
     * @throws PermissionException
     */
    public abstract void restrict(CommandSender source, Address address, User user, Duration duration, String reason, boolean silent) throws RejectedException, PermissionException;
}
