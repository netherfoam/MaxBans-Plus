package org.maxgamer.maxbans.service;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.maxgamer.maxbans.orm.User;

/**
 * @author netherfoam
 */
public class LocatorService {
    private Server server;
    private UserService userService;

    public LocatorService(Server server, UserService userService) {
        this.server = server;
        this.userService = userService;
    }

    public User user(String name) {
        User user = userService.get(name);
        if(user != null) return user;

        user = userService.get(name + "%");
        if(user != null) return user;

        Player p = server.getPlayerExact(name);
        if(p != null) {
            user = userService.getOrCreate(p);
            return user;
        }

        p = server.getPlayer(name);
        if(p != null) {
            user = userService.getOrCreate(p);
            return user;
        }

        // Couldn't find any user anywhere with that name
        return null;
    }

    public Player player(User user) {
        return server.getPlayer(user.getId());
    }
}
