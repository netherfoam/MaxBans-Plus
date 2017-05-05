package org.maxgamer.maxbans.repository;

import org.hibernate.SessionFactory;
import org.maxgamer.maxbans.orm.User;

import java.util.UUID;

/**
 * @author Dirk Jamieson
 */
public class UserRepository extends Repository<UUID, User> {
    public UserRepository(SessionFactory factory) {
        super(factory, UUID.class, User.class);
    }
    
    public User findByName(String name) {
        return (User) session().createQuery("SELECT u FROM Users u WHERE u.name LIKE :name")
                .setParameter("name", name)
                .iterate()
                .next();
    }
}
