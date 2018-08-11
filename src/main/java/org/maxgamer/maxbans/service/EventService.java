package org.maxgamer.maxbans.service;

import org.bukkit.plugin.PluginManager;
import org.maxgamer.maxbans.MaxBansPlus;
import org.maxgamer.maxbans.event.AbstractMaxBansEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class EventService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MaxBansPlus.class);

    private PluginManager pluginManager;

    @Inject
    public EventService(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    public void call(AbstractMaxBansEvent event) {
        LOGGER.debug("Raising Event: " + event);
        pluginManager.callEvent(event);
    }
}
