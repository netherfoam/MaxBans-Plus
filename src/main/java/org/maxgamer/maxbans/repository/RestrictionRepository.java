package org.maxgamer.maxbans.repository;

import org.hibernate.criterion.Restrictions;
import org.maxgamer.maxbans.orm.Restriction;
import org.maxgamer.maxbans.transaction.Transactor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * @author netherfoam
 */
public class RestrictionRepository<R extends Restriction> extends Repository<UUID, R> {
    public RestrictionRepository(Transactor worker, Class<R> entityClass) {
        super(worker, UUID.class, entityClass);
    }

    public List<R> findAllSince(Instant createdAt) {
        return getWorker().retrieve(session -> {
            return (List<R>) session.createCriteria(entityClass, "e")
                    .add(Restrictions.ge("createdAt", createdAt))
                    .list();
        });
    }
}
