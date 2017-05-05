package org.maxgamer.maxbans.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.io.Serializable;

/**
 * @author Dirk Jamieson
 */
public abstract class Repository<ID extends Serializable, T> {
    private Class<ID> idClass;
    private Class<T> entityClass;
    private SessionFactory factory;
    private Session session;

    public Repository(SessionFactory factory, Class<ID> idClass, Class<T> entityClass) {
        this.factory = factory;
        this.idClass = idClass;
        this.entityClass = entityClass;
    }

    protected synchronized Session session() {
        if(session != null && session.isConnected()) {
            return session;
        }
        
        session = factory.openSession();
        
        return session; 
    }

    public T find(ID id) {
        return session().get(entityClass, id);
    }
    
    public void persist(T t) {
        session().persist(t);
        session().flush();
    }
    
    public void save(T t) {
        session().saveOrUpdate(t);
        session().flush();
    }
}
