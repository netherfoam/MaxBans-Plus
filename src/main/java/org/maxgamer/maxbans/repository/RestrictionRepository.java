package org.maxgamer.maxbans.repository;

import org.maxgamer.maxbans.orm.Restriction;
import org.maxgamer.maxbans.transaction.Transactor;

import java.util.UUID;

/**
 * @author netherfoam
 */
public class RestrictionRepository<R extends Restriction> extends Repository<UUID, R> {
    public RestrictionRepository(Transactor worker, Class<R> entityClass) {
        super(worker, UUID.class, entityClass);
    }
}
