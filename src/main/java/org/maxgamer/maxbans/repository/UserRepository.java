package org.maxgamer.maxbans.repository;

import org.maxgamer.maxbans.orm.User;
import org.maxgamer.maxbans.transaction.TransactionLayer;

import javax.inject.Inject;
import java.util.Iterator;
import java.util.UUID;

/**
 * @author Dirk Jamieson
 */
public class UserRepository extends Repository<UUID, User> {
    @Inject
    public UserRepository() {
        super(UUID.class, User.class);
    }
    
    public User findByAlias(String name) {
        try (TransactionLayer tx = worker.transact()) {
            Iterator iterator = tx.getEntityManager().createQuery("SELECT u FROM User u WHERE u.alias LIKE :name")
                    .setParameter("name", name.toLowerCase())
                    .getResultList()
                    .iterator();

            if(!iterator.hasNext()) return null;

            return (User) iterator.next();
        }
    }
}
