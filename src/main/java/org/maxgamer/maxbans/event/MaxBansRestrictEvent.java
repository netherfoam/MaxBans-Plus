package org.maxgamer.maxbans.event;

import org.bukkit.event.Cancellable;
import org.maxgamer.maxbans.orm.Tenant;
import org.maxgamer.maxbans.orm.User;

public class MaxBansRestrictEvent<T extends Tenant> extends AbstractMaxBansEvent implements Cancellable {
    private User admin;
    private T target;
    private boolean cancelled;

    public MaxBansRestrictEvent(User admin, T target) {
        this.admin = admin;
        this.target = target;
    }

    /**
     * @return true if this restriction was issued by a player
     */
    public boolean isPlayerAdministered() {
        return admin != null;
    }

    public User getAdmin() {
        return admin;
    }

    public T getTarget() {
        return target;
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
