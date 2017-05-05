package org.maxgamer.maxbans.repository;

import org.maxgamer.maxbans.orm.Mute;
import org.maxgamer.maxbans.transaction.Transactor;

import java.util.UUID;

/**
 * @author Dirk Jamieson
 */
public class MuteRepository extends Repository<UUID, Mute> {
    public MuteRepository(Transactor worker) {
        super(worker, UUID.class, Mute.class);
    }
}
