package org.maxgamer.maxbans.orm;

/**
 * A user or IP address that can be banned
 */
public interface Tenant {
    /**
     * The name of the entity that is being targetted
     * @return the name
     */
    String getName();
}
