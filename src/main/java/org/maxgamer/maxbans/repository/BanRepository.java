package org.maxgamer.maxbans.repository;

import org.maxgamer.maxbans.orm.Ban;
import org.maxgamer.maxbans.transaction.Transactor;

import java.util.UUID;

/**
 * @author Dirk Jamieson
 */
public class BanRepository extends Repository<UUID, Ban> {
    public BanRepository(Transactor worker) {
        super(worker, UUID.class, Ban.class);
    }
}
