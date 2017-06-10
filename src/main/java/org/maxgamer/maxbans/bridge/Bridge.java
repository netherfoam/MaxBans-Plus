package org.maxgamer.maxbans.bridge;

import org.maxgamer.maxbans.context.PluginContext;

/**
 * @author netherfoam
 */
public interface Bridge {
    void pull(PluginContext into) throws Exception;
}
