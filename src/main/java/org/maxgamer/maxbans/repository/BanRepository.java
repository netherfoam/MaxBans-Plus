package org.maxgamer.maxbans.repository;

import org.maxgamer.maxbans.orm.Ban;
import org.maxgamer.maxbans.transaction.Transactor;

/**
 * @author Dirk Jamieson
 */
public class BanRepository extends RestrictionRepository<Ban> {
    public BanRepository(Transactor worker) {
        super(worker, Ban.class);
    }
}
