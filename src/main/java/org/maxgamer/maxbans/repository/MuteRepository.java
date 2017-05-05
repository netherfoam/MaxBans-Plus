package org.maxgamer.maxbans.repository;

import org.hibernate.SessionFactory;
import org.maxgamer.maxbans.orm.Mute;

import java.util.UUID;

/**
 * @author Dirk Jamieson
 */
public class MuteRepository extends Repository<UUID, Mute> {
    public MuteRepository(SessionFactory factory) {
        super(factory, UUID.class, Mute.class);
    }
}
