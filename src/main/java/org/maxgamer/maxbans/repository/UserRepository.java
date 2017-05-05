package org.maxgamer.maxbans.repository;

import org.maxgamer.maxbans.orm.User;
import org.maxgamer.maxbans.transaction.Transactor;

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
            return (User) session.createQuery("SELECT u FROM Users u WHERE u.name LIKE :name")
                    .setParameter("name", name)
                    .iterate()
                    .next();
        });
    }
}
