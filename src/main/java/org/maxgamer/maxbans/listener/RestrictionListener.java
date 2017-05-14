package org.maxgamer.maxbans.listener;

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

    public RestrictionListener(Transactor transactor, UserService userService, LockdownService lockdownService, BroadcastService broadcastService, AddressService addressService, Locale locale) {
        this.transactor = transactor;
        this.userService = userService;
        this.lockdownService = lockdownService;
        this.broadcastService = broadcastService;
        this.addressService = addressService;
        this.locale = locale;
    }

    @EventHandler
    public void onJoin(PlayerLoginEvent e) {
        transactor.work(session -> {
            User user = userService.getOrCreate(e.getPlayer());

            try {
                userService.onJoin(user);
            } catch (RejectedException r) {
                e.setResult(PlayerLoginEvent.Result.KICK_OTHER);
                e.setKickMessage(r.getMessage(locale));

                broadcastService.moderators("banned", Duration.ofMinutes(3), r.toBuilder(locale).get("notification.banned"));
            }

            try {
                lockdownService.onJoin(user);
            } catch (RejectedException r) {
                e.setResult(PlayerLoginEvent.Result.KICK_OTHER);
                e.setKickMessage(r.getMessage(locale));

                broadcastService.moderators("lockdown", Duration.ofMinutes(3), r.toBuilder(locale).get("notification.lockdown"));
            }

            if(e.getResult() == PlayerLoginEvent.Result.ALLOWED) {
                addressService.onJoin(user, e.getAddress().getHostAddress());
            }
        });
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        transactor.work(session -> {
            User user = userService.get(e.getPlayer());
            if (user == null) return;

            try {
                userService.onChat(user);
            } catch (RejectedException r) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(r.getMessage(locale));

                broadcastService.moderators("muted", Duration.ofMinutes(3), r.toBuilder(locale).get("notification.muted"));
            }
        });
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        transactor.work(session -> {
            User user = userService.get(e.getPlayer());
            if (user == null) return;

            try {
                userService.onCommand(user, e.getMessage());
            } catch (RejectedException r) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(r.getMessage(locale));

                broadcastService.moderators("muted", Duration.ofMinutes(3), r.toBuilder(locale).get("notification.muted"));
            }
        });
    }
}
