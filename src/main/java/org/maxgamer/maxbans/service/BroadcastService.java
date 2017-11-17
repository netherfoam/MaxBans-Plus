package org.maxgamer.maxbans.service;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.maxgamer.maxbans.locale.Locale;
import org.maxgamer.maxbans.util.Permissions;

import javax.inject.Inject;
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
    private Locale locale;

    @Inject
    public BroadcastService(Server server, Locale locale) {
        this.server = server;
        this.locale = locale;
    }

    public void moderators(String message) {
        server.broadcast(message, MODERATOR_PERMISSION);
    }

    /**
     * Broadcasts the given message to the appropriate players
     * @param message the message to broadcast
     * @param silent true if the broadcast should only notify moderators, false if it should notify players
     * @param required the list of players who must, indisputably, be sent the message. May contain nulls.
     */
    public void broadcast(String message, boolean silent, CommandSender... required) {
        String permission;

        if(silent) {
            permission = Permissions.SEE_SILENT;

            // Attach our silence prefix & suffix
            if (locale.has("silent.prefix")) {
                message = locale.get().get("silent.prefix") + message;
            }

            if (locale.has("silent.suffix")) {
                message = message + locale.get().get("silent.suffix");
            }
        } else {
            permission = Permissions.SEE_BROADCAST;
        }

        server.broadcast(message, permission);
        for(CommandSender involved : required) {
            // If the target already has permission, we've already messaged them!
            if(involved == null || involved.hasPermission(permission)) continue;

            // Target doesn't have permission so wasn't notified by the broadcast!
            involved.sendMessage(message);
        }
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
