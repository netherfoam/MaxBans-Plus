package org.maxgamer.maxbans.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.maxgamer.maxbans.exception.RejectedException;
import org.maxgamer.maxbans.locale.Locale;
import org.maxgamer.maxbans.orm.User;
import org.maxgamer.maxbans.service.AddressService;
import org.maxgamer.maxbans.service.BroadcastService;
import org.maxgamer.maxbans.service.LockdownService;
import org.maxgamer.maxbans.service.UserService;
import org.maxgamer.maxbans.transaction.Transactor;

import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Dirk Jamieson
 */
public class RestrictionListener implements Listener {
    private Transactor transactor;
    private UserService userService;
    private LockdownService lockdownService;
    private BroadcastService broadcastService;
    private AddressService addressService;
    private Locale locale;
    private Logger logger;

    public RestrictionListener(Transactor transactor, UserService userService, LockdownService lockdownService, BroadcastService broadcastService, AddressService addressService, Locale locale, Logger logger) {
        this.transactor = transactor;
        this.userService = userService;
        this.lockdownService = lockdownService;
        this.broadcastService = broadcastService;
        this.addressService = addressService;
        this.locale = locale;
        this.logger = logger;
    }

    public void onJoin(Player player, String address) throws RejectedException {
        User user = userService.getOrCreate(player);

        try {
            userService.onJoin(user);
        } catch (RejectedException r) {
            broadcastService.moderators("banned", Duration.ofMinutes(3), r.toBuilder(locale).get("notification.banned"));

            throw r;
        }

        try {
            addressService.onJoin(user, address);
        } catch (RejectedException r) {
            broadcastService.moderators("banned", Duration.ofMinutes(3), r.toBuilder(locale).get("notification.ipbanned"));

            throw r;
        }

        try {
            lockdownService.onJoin(user);
        } catch (RejectedException r) {
            broadcastService.moderators("lockdown", Duration.ofMinutes(3), r.toBuilder(locale).get("notification.lockdown"));
            throw r;
        }
    }

    @EventHandler
    public void onJoin(PlayerLoginEvent e) {
        try {
            transactor.work(session -> {
                try {
                    onJoin(e.getPlayer(), e.getAddress().getHostAddress());
                } catch (RejectedException r) {
                    e.setResult(PlayerLoginEvent.Result.KICK_OTHER);
                    e.setKickMessage(r.getMessage(locale));
                }
            });
        } catch (Throwable t) {
            logger.log(Level.SEVERE, "An error occurred when a player tried to join", t);

            throw t;
        }
    }

    public void onChat(Player player) throws RejectedException {
        User user = userService.getOrCreate(player);

        try {
            userService.onChat(user);
        } catch (RejectedException r) {
            r.with("name", player.getName());

            broadcastService.moderators("muted", Duration.ofMinutes(3), r.toBuilder(locale).get("notification.muted"));
            throw r;
        }

        try {
            addressService.onChat(addressService.getOrCreate(player.getAddress().getAddress().getHostAddress()));
        } catch (RejectedException r) {
            r.with("name", player.getName());

            broadcastService.moderators("muted", Duration.ofMinutes(3), r.toBuilder(locale).get("notification.ipmuted"));
            throw r;
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        try {
            transactor.work(session -> {
                try {
                    onChat(e.getPlayer());
                } catch (RejectedException r) {
                    e.setCancelled(true);
                    e.getPlayer().sendMessage(r.getMessage(locale));
                }
            });
        } catch (Throwable t) {
            logger.log(Level.SEVERE, "An error occurred when a player tried to chat", t);

            throw t;
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        try {
            if(!userService.isChatCommand(e.getMessage())) return;

            transactor.work(session -> {
                try {
                    onChat(e.getPlayer());
                } catch (RejectedException r) {
                    e.setCancelled(true);
                    e.getPlayer().sendMessage(r.getMessage(locale));
                }
            });
        } catch (Throwable t) {
            logger.log(Level.SEVERE, "An error occurred when a player tried to use a command", t);

            throw t;
        }
    }
}
