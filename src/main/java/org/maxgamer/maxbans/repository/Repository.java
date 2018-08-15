package org.maxgamer.maxbans.repository;

import org.maxgamer.maxbans.transaction.TransactionLayer;
import org.maxgamer.maxbans.transaction.Transactor;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

/**
 * @author Dirk Jamieson
 */
public abstract class Repository<ID extends Serializable, T> {
    protected final Class<ID> idClass;
    protected final Class<T> entityClass;

    @Inject
    protected Transactor worker;

    public Repository(Class<ID> idClass, Class<T> entityClass) {
        this.idClass = idClass;
        this.entityClass = entityClass;
    }

    public T find(ID id) {
        try (TransactionLayer tx = worker.transact()) {
            return tx.getEntityManager().find(entityClass, id);
        }
    }
    
    public void persist(T t) {
        try (TransactionLayer tx = worker.transact()) {
            tx.getEntityManager().persist(t);
        }
    }
    
    public void save(T t) {
        try (TransactionLayer tx = worker.transact()) {
            tx.getEntityManager().persist(t);
        }
    }

    public List<T> findAll() {
        try (TransactionLayer tx = worker.transact()) {
            return tx.getEntityManager().createQuery("e", entityClass).getResultList();
        }
    }

    public Transactor getWorker() {
        return worker;
    }
}
