package org.maxgamer.maxbans.service;

import org.maxgamer.maxbans.locale.Locale;
import org.maxgamer.maxbans.locale.Message;
import org.maxgamer.maxbans.locale.MessageBuilder;
import org.maxgamer.maxbans.orm.*;
import org.maxgamer.maxbans.transaction.TransactionLayer;
import org.maxgamer.maxbans.transaction.Transactor;
import org.maxgamer.maxbans.util.MessageUtil;

import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * History service helps generate the messages used to describe recent occurrences to moderators
 */
public class HistoryService {
    /**
     * The page size
     */
    private static final int LIMIT = 15;

    /**
     * The transactor to use to perform a transaction
     */
    private Transactor transactor;

    /**
     * The locale we use for history message generation
     */
    private Locale locale;

    @Inject
    public HistoryService(Transactor transactor, Locale locale) {
        this.transactor = transactor;
        this.locale = locale;
    }

    /**
     * Fetch the history for the given moderator
     * @param page the page, zero indexed
     * @param source the moderator to view the past actions of
     * @return the page of actions
     */
    public List<Message> getHistory(int page, User source) {
        try (TransactionLayer tx = transactor.transact()) {
            return describe(getBySender(page, source));
        }
    }

    /**
     * Fetch the history for the given page, globally
     * @param page the page number, zero indexed
     * @return the page of history
     */
    public List<Message> getHistory(int page) {
        try (TransactionLayer tx = transactor.transact()) {
            return describe(getAll(page));
        }
    }

    private List<Restriction> getBySender(int page, User user) {
        try (TransactionLayer tx = transactor.transact()) {
            CriteriaBuilder cb = tx.getEntityManager().getCriteriaBuilder();
            CriteriaQuery<Restriction> query = cb.createQuery(Restriction.class);
            Root<Restriction> root = query.from(Restriction.class);

            if (user != null) {
                query.where(cb.equal(root.get("source"), user));
            } else {
                query.where(cb.isNull(root.get("source")));
            }

            query.orderBy(cb.desc(root.get("created")));

            return tx.getEntityManager().createQuery(query)
                    .setFirstResult(page * LIMIT)
                    .setMaxResults(LIMIT)
                    .getResultList();
        }
    }

    private List<Restriction> getAll(int page) {
        try (TransactionLayer tx = transactor.transact()) {
            CriteriaBuilder cb = tx.getEntityManager().getCriteriaBuilder();
            CriteriaQuery<Restriction> query = cb.createQuery(Restriction.class);
            Root<Restriction> root = query.from(Restriction.class);

            query.orderBy(cb.desc(root.get("created")));

            return tx.getEntityManager().createQuery(query)
                    .setFirstResult(page * LIMIT)
                    .setMaxResults(LIMIT)
                    .getResultList();
        }
    }

    private List<Message> describe(List<Restriction> restrictions) {
        List<Message> messages = new ArrayList<>(restrictions.size());

        for (Restriction restriction : restrictions) {
            MessageBuilder builder = contextualise(restriction);
            Message message = get(builder, restriction);
            messages.add(message);
        }

        return messages;
    }

    private MessageBuilder contextualise(Restriction restriction) {
        Tenant tenant = restriction.getTenant();

        MessageBuilder builder = locale.get();
        MessageUtil.inject(builder, tenant);
        MessageUtil.inject(builder, restriction);

        return builder;
    }

    private Message get(MessageBuilder builder, Restriction restriction) {
        if (restriction instanceof Mute) {
            return builder.get("history.mute");
        }

        if (restriction instanceof Ban) {
            return builder.get("history.ban");
        }

        if (restriction instanceof Warning) {
            return builder.get("history.warn");
        }

        throw new IllegalArgumentException("No such restriction type registered: " + restriction);
    }
}
