package org.maxgamer.maxbans.repository;

import org.hibernate.SessionFactory;
import org.maxgamer.maxbans.orm.Ban;
import org.maxgamer.maxbans.orm.User;

import java.util.UUID;

/**
 * @author Dirk Jamieson
 */
public class BanRepository extends Repository<UUID, Ban> {
    public BanRepository(SessionFactory factory) {
        super(factory, UUID.class, Ban.class);
    }
}
