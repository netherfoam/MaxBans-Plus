package org.maxgamer.maxbans.event;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class KickUserEvent extends AbstractMaxBansEvent implements Cancellable {
    private CommandSender source;
    private Player target;
    private boolean cancelled;

    public KickUserEvent(CommandSender source, Player target) {
        this.target = target;
        this.source = source;
    }

    public Player getTarget() {
        return target;
    }

    public void setTarget(Player target) {
        this.target = target;
    }

    public CommandSender getSource() {
        return source;
    }

    public void setSource(Player source) {
        this.source = source;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
