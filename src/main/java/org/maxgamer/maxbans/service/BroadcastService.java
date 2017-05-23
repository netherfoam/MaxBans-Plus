package org.maxgamer.maxbans.service;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.maxgamer.maxbans.util.Permissions;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Dirk Jamieson <dirk@redeye.co>
 */
public class BroadcastService {
    private static final String MODERATOR_PERMISSION = "maxbans.mod";
    private Server server;
    private Map<Object, Instant> firewall = new HashMap<>();

    public BroadcastService(Server server) {
        this.server = server;
    }

    public void broadcast(String message, boolean silent) {
        if(silent) {
            server.broadcast(message, Permissions.SEE_SILENT);
        } else {
            server.broadcast(message, Permissions.SEE_BROADCAST);
        }
    }

    public void moderators(String message) {
        server.broadcast(message, MODERATOR_PERMISSION);
    }

    /**
     * Notifies all moderators on the server with the given message. This has a "spam firewall" behaviour,
     * where the given amount of time is required to pass before the message will be received again. This
     * is to prevent players from trying to spam moderators.
     *
     * The key is the identifier for the event. Any events with the same identifiers will have to respect
     * each others "timeout" requirement.
     *
     * @param key the key describing the event. Some identifier for the event.
     * @param timeout the minimum time between notifications
     * @param message the message to deliver to moderators
     */
    public void moderators(Object key, Duration timeout, String message) {
        if(key == null) {
            throw new IllegalArgumentException("Key may not be null");
        }

        if(key instanceof Player) {
            throw new IllegalArgumentException("Players make bad keys");
        }

        Instant old = firewall.get(key);
        if(old != null) {
            if(old.plus(timeout).isAfter(Instant.now())) {
                // The previous message was still too recent
                return;
            }
        }

        // Block this message from going out again
        firewall.put(key, Instant.now().plus(timeout));

        moderators(message);
    }
}
