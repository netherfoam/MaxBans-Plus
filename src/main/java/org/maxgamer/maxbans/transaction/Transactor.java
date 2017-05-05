package org.maxgamer.maxbans.transaction;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 * @author netherfoam
 */
public class Transactor {
    public interface Job<T> {
        T run(Session session);
    }

    public interface VoidJob {
        void run(Session session);
    }

    private static ThreadLocal<Session> sessions = new ThreadLocal<>();

    private SessionFactory factory;

    public Transactor(SessionFactory factory) {
        this.factory = factory;
    }

    public void work(VoidJob job) {
        // Convenience method so that we don't have to return a value
        retrieve(s -> {
            job.run(s);
            return null;
        });
    }

    public <T> T retrieve(Job<T> job) {
        boolean created = false;
        Session session = sessions.get();
        Transaction transaction;
        if(session == null) {
            session = factory.openSession();
            sessions.set(session);
            transaction = session.beginTransaction();
            created = true;
        } else {
            transaction = session.getTransaction();
        }

        T value;
        try {
            value = job.run(session);
            session.flush();
        } catch (Throwable t) {
            transaction.rollback();

            throw t;
        }

        if(created) {
            transaction.commit();
            session.close();
            sessions.set(null);
        }

        return value;
    }
}
