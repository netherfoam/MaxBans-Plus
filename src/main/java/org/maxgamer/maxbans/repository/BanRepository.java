package org.maxgamer.maxbans.repository;

import org.maxgamer.maxbans.orm.Ban;

import javax.inject.Inject;

/**
 * @author Dirk Jamieson
 */
public class BanRepository extends RestrictionRepository<Ban> {
    @Inject
    public BanRepository() {
        super(Ban.class);
    }
}
