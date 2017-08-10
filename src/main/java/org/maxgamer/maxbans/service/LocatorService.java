package org.maxgamer.maxbans.service;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.maxgamer.maxbans.orm.Address;
import org.maxgamer.maxbans.orm.User;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;

/**
 * @author netherfoam
 */
public class LocatorService {
    private Server server;
    private UserService userService;

    @Inject
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

    public Player player(String name) {
        return server.getPlayer(name);
    }

    public List<Player> players(Address address) {
        LinkedList<Player> list = new LinkedList<>();
        for(Player player : server.getOnlinePlayers()) {
            if(player.getAddress().getAddress().getHostAddress().equals(address.getHost())) {
                list.add(player);
            }
        }

        return list;
    }
}
