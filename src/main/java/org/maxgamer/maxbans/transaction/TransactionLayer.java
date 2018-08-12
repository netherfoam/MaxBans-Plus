package org.maxgamer.maxbans.transaction;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.maxgamer.maxbans.exception.TransactionException;

import javax.persistence.EntityManager;
import java.io.Closeable;

/**
 * Represents a single transaction layer. The root transaction layer is responsible for committing
 * the final result, where any nested transactions are disregarded when closed.
 */
public class TransactionLayer implements Closeable {
    private ThreadLocal<Session> sessions;
    private Session session;
    private Transaction transaction;

    private boolean opened;

    public TransactionLayer(ThreadLocal<Session> sessions, SessionFactory factory) {
        this.sessions = sessions;
        this.session = sessions.get();

        if (this.session == null) {
            this.session = factory.openSession();
            sessions.set(this.session);
            this.transaction = this.session.beginTransaction();

            this.opened = true;
        } else {
            this.opened = false;
        }
    }

    public EntityManager getEntityManager() {
        return session;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public boolean isRoot() {
        return opened;
    }

    @Override
    public void close() {
        if (!opened) {
            // We aren't responsible for managing the session
            return;
        }

        try {
            session.flush();
        } catch (Throwable t) {
            transaction.rollback();

            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            }

            throw new TransactionException(t);
        } finally {
            sessions.set(null);

            if (transaction.getStatus() != TransactionStatus.ROLLED_BACK) {
                transaction.commit();
            }
            session.close();
        }
    }
}
