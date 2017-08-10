package org.maxgamer.maxbans.repository;

import org.maxgamer.maxbans.orm.Mute;

import javax.inject.Inject;

/**
 * @author Dirk Jamieson
 */
public class MuteRepository extends RestrictionRepository<Mute> {
    @Inject
    public MuteRepository() {
        super(Mute.class);
    }
}
