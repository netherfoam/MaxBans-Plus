package org.maxgamer.maxbans.service;

import org.bukkit.Server;
import org.maxgamer.maxbans.util.Permissions;

/**
 * @author Dirk Jamieson <dirk@redeye.co>
 */
public class BroadcastService {
    private Server server;

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
}
