package org.maxgamer.maxbans.transaction;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Transactor provides a try-catch resource for opening transaction layers
 *
 * @author netherfoam
 */
@Singleton
public class Transactor {
    /**
     * All of the sessions which have been opened as transaction layers by this Transactor
     */
    private ThreadLocal<Session> sessions = new ThreadLocal<>();

    /**
     * The session factory we use to construct new sessions
     */
    private SessionFactory factory;

    @Inject
    public Transactor(SessionFactory factory) {
        this.factory = factory;
    }

    /**
     * Open a new TransactionLayer for a try-catch block. If this is a root transaction,
     * when closed, the transaction will be committed. Otherwise, it will have no effect.
     *
     * @return the transaction layer
     */
    public TransactionLayer transact() {
        return new TransactionLayer(sessions, factory);
    }
}
