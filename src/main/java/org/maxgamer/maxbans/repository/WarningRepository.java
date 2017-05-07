package org.maxgamer.maxbans.repository;

import org.maxgamer.maxbans.orm.Warning;
import org.maxgamer.maxbans.transaction.Transactor;

import java.util.UUID;

/**
 * @author netherfoam
 */
public class WarningRepository extends Repository<UUID, Warning> {
    public WarningRepository(Transactor worker) {
        super(worker, UUID.class, Warning.class);
    }
}
