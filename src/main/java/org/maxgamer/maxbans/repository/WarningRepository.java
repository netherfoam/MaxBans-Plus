package org.maxgamer.maxbans.repository;

import org.maxgamer.maxbans.orm.Warning;

import javax.inject.Inject;

/**
 * @author netherfoam
 */
public class WarningRepository extends RestrictionRepository<Warning> {
    @Inject
    public WarningRepository() {
        super(Warning.class);
    }
}
