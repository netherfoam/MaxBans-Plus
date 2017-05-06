package org.maxgamer.maxbans.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.maxgamer.maxbans.exception.RejectedException;
import org.maxgamer.maxbans.orm.User;
import org.maxgamer.maxbans.service.UserService;

/**
 * @author Dirk Jamieson
 */
public class RestrictionListener implements Listener {
    private UserService userService;

    public RestrictionListener(UserService userService) {
        this.userService = userService;
    }

    @EventHandler
    public void onJoin(PlayerLoginEvent e) {
        User user = userService.getOrCreate(e.getPlayer());
        
        try {
            userService.onJoin(user);
        } catch (RejectedException r) {
            e.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            e.setKickMessage(r.getMessage());
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        User user = userService.get(e.getPlayer());
        if(user == null) return;
        
        try {
            userService.onChat(user);
        } catch (RejectedException r) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(r.getMessage());
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        User user = userService.get(e.getPlayer());
        if(user == null) return;

        try {
            userService.onCommand(user, e.getMessage());
        } catch (RejectedException r) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(r.getMessage());
        }
    }
}
