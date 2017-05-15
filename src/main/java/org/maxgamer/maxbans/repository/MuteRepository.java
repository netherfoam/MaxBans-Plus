package org.maxgamer.maxbans.repository;

import org.maxgamer.maxbans.orm.Mute;
import org.maxgamer.maxbans.transaction.Transactor;

/**
 * @author Dirk Jamieson
 */
public class MuteRepository extends RestrictionRepository<Mute> {
    public MuteRepository(Transactor worker) {
        super(worker, Mute.class);
    }
}
