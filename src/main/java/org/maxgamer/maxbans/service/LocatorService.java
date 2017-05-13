package org.maxgamer.maxbans.service;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.maxgamer.maxbans.orm.User;
import org.maxgamer.maxbans.repository.UserRepository;

import java.util.List;

/**
 * @author netherfoam
 */
public class LocatorService {
    private Server server;
    private UserService userService;
    private UserRepository userRepository;

    public LocatorService(Server server, UserService userService) {
        this.server = server;
        this.userService = userService;
    }

    public List<User> users(String prefix, int limit) {
        return userRepository.findByName(prefix, limit);
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
}
