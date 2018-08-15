package org.maxgamer.maxbans.service;

import org.bukkit.plugin.PluginManager;
import org.maxgamer.maxbans.event.AbstractMaxBansEvent;

import javax.inject.Inject;

public class EventService {
    private PluginManager pluginManager;

    @Inject
    public EventService(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    public void call(AbstractMaxBansEvent event) {
        pluginManager.callEvent(event);
    }
}
