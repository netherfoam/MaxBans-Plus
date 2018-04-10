package org.maxgamer.maxbans.service;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.maxgamer.maxbans.locale.Locale;
import org.maxgamer.maxbans.locale.MessageBuilder;
import org.maxgamer.maxbans.orm.*;
import org.maxgamer.maxbans.transaction.TransactionLayer;
import org.maxgamer.maxbans.transaction.Transactor;
import org.maxgamer.maxbans.util.MessageUtil;

import javax.inject.Inject;
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
    public List<String> getHistory(int page, User source) {
        try (TransactionLayer tx = transactor.transact()) {
            return describe(getBySender(page, source));
        }
    }

    /**
     * Fetch the history for the given page, globally
     * @param page the page number, zero indexed
     * @return the page of history
     */
    public List<String> getHistory(int page) {
        try (TransactionLayer tx = transactor.transact()) {
            return describe(getAll(page));
        }
    }

    private List<Restriction> getBySender(int page, User user) {
        try (TransactionLayer tx = transactor.transact()) {
            Criterion userMustMatch;
            if (user != null) {
                userMustMatch = Restrictions.eq("source", user);
            } else {
                userMustMatch = Restrictions.isNull("source");
            }

            List<?> list = tx.getSession().createCriteria(Restriction.class, "r")
                    .add(userMustMatch)
                    .addOrder(Order.desc("created"))
                    .setFirstResult(page * LIMIT)
                    .setMaxResults(LIMIT)
                    .list();

            return (List<Restriction>) list;
        }
    }

    private List<Restriction> getAll(int page) {
        try (TransactionLayer tx = transactor.transact()) {
            List<?> list = tx.getSession().createCriteria(Restriction.class, "r")
                    .addOrder(Order.desc("created"))
                    .setFirstResult(page * LIMIT)
                    .setMaxResults(LIMIT)
                    .list();

            return (List<Restriction>) list;
        }
    }

    private List<String> describe(List<Restriction> restrictions) {
        List<String> messages = new ArrayList<>(restrictions.size());

        for (Restriction restriction : restrictions) {
            MessageBuilder builder = contextualise(restriction);
            String message = get(builder, restriction);
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

    private String get(MessageBuilder builder, Restriction restriction) {
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
