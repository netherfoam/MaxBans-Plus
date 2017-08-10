package org.maxgamer.maxbans.repository;

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
        return worker.retrieve((session) -> session.get(entityClass, id));
    }
    
    public void persist(T t) {
        worker.work((session) -> session.persist(t));
    }
    
    public void save(T t) {
        worker.work((session) -> session.saveOrUpdate(t));
    }

    public List<T> findAll() {
        return (List<T>) worker.retrieve(session -> session.createCriteria(entityClass).list());
    }

    public Transactor getWorker() {
        return worker;
    }
}
