package org.maxgamer.maxbans.repository;

import org.maxgamer.maxbans.orm.Restriction;

import java.util.UUID;

/**
 * @author netherfoam
 */
public class RestrictionRepository<R extends Restriction> extends Repository<UUID, R> {
    public RestrictionRepository(Class<R> entityClass) {
        super(UUID.class, entityClass);
    }
}
