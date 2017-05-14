package org.maxgamer.maxbans.repository;

import org.maxgamer.maxbans.orm.User;
import org.maxgamer.maxbans.transaction.Transactor;

import java.util.Iterator;
import java.util.UUID;

/**
 * @author Dirk Jamieson
 */
public class UserRepository extends Repository<UUID, User> {
    public UserRepository(Transactor worker) {
        super(worker, UUID.class, User.class);
    }
    
    public User findByName(String name) {
        return worker.retrieve(session -> {
            Iterator iterator = session.createQuery("SELECT u FROM User u WHERE u.name LIKE :name")
                    .setParameter("name", name)
                    .iterate();

            if(!iterator.hasNext()) return null;

            return (User) iterator.next();
        });
    }
}
