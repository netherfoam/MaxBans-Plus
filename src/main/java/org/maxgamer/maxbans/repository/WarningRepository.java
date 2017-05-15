package org.maxgamer.maxbans.repository;

import org.maxgamer.maxbans.orm.Warning;
import org.maxgamer.maxbans.transaction.Transactor;

/**
 * @author netherfoam
 */
public class WarningRepository extends RestrictionRepository<Warning> {
    public WarningRepository(Transactor worker) {
        super(worker, Warning.class);
    }
}
