package org.maxgamer.maxbans.repository;

import org.maxgamer.maxbans.orm.User;
import org.maxgamer.maxbans.transaction.Transactor;

import java.util.List;
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
            return (User) session.createQuery("SELECT u FROM Users u WHERE u.name LIKE :name ORDER BY lastActive DESC")
                    .setParameter("name", name)
                    .iterate()
                    .next();
        });
    }

    public List<User> findByName(String prefix, int limit) {
        return worker.retrieve(session -> {
            return (List<User>) session.createQuery("SELECT u FROM Users u WHERE u.name LIKE :name ORDER BY lastActive DESC")
                    .setParameter("name", prefix + "%")
                    .setMaxResults(limit)
                    .list();
        });
    }
}
