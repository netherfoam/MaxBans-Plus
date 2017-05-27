package org.maxgamer.maxbans.service;

import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.maxgamer.maxbans.exception.RejectedException;
import org.maxgamer.maxbans.locale.Locale;
import org.maxgamer.maxbans.locale.MessageBuilder;
import org.maxgamer.maxbans.orm.User;
import org.maxgamer.maxbans.util.Lockdown;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

/**
 * @author netherfoam
 */
public class LockdownService {
    private Server server;

    private UserService userService;
    private BroadcastService broadcastService;

    private FileConfiguration file;
    private Lockdown state;
    private String reason;

    public LockdownService(Server server, UserService userService, BroadcastService broadcastService, FileConfiguration file) {
        this.server = server;
        this.userService = userService;
        this.broadcastService = broadcastService;
        this.file = file;

        String type = file.getString("state", "off");

        this.state = Lockdown.get(type);
        if(this.state == null) {
            this.state = Lockdown.OFF;
        }

        this.reason = file.getString("reason");
    }

    public void onJoin(User user) throws RejectedException {
        if(isAllowed(user, true)) {
            return;
        }

        // Server has some kind of lockdown
        throw new RejectedException("lockdown.message")
                .with("reason", reason);
    }

    public boolean isAllowed(User user, boolean isJoining) {
        switch(state) {
            case ALL:
                // Nobody is allowed to join
                return false;
            case JOIN:
                // If you're joining, you're not allowed
                return !isJoining;
            case NEW:
                // If your account is old, you're allowed
                return user.getFirstActive().isBefore(newQualifier());
            case OFF:
                // Anyone's allowed: Happy days!
                return true;
        }

        return true;
    }

    public MessageBuilder lockdown(User source, String name, String reason, Locale locale) throws RejectedException {
        Lockdown type = Lockdown.get(name);

        if(type == null) {
            throw new RejectedException("No such lockdown: " + name + ", options are: " + Arrays.toString(Lockdown.values()));
        }

        if(source != null && !isAllowed(source, false)) {
            throw new RejectedException(
                    "That would prevent you from logging back in: " +
                    "You wouldn't be able to turn the lockdown off! " +
                    "Only console users may do that."
            );
        }

        this.state = type;
        this.reason = reason;

        file.set("state", state.toString());
        file.set("reason", reason);

        MessageBuilder message = locale.get()
                .with("type", state.toString().toLowerCase())
                .with("reason", reason)
                .with("description", state.description())
                .with("source", source == null ? "Console" : source.getName());

        // Kick all players who no longer are allowed (new or all players)
        if(type == Lockdown.ALL || type == Lockdown.NEW) {
            for(Player player : server.getOnlinePlayers()) {
                User user = userService.getOrCreate(player);
                if(!isAllowed(user, false)) {
                    player.kickPlayer(message.get("lockdown.message"));
                }
            }
        }

        return message;
    }

    public Instant newQualifier() {
        return Instant.now().minus(30, ChronoUnit.MINUTES);
    }

    public Lockdown getState() {
        return state;
    }
}
