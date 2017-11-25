package org.maxgamer.maxbans.service;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.maxgamer.maxbans.locale.Locale;
import org.maxgamer.maxbans.locale.MessageBuilder;
import org.maxgamer.maxbans.orm.*;
import org.maxgamer.maxbans.transaction.Transactor;
import org.maxgamer.maxbans.util.MessageUtil;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class HistoryService {
    private static final int LIMIT = 15;

    private Transactor transactor;
    private Locale locale;

    @Inject
    public HistoryService(Transactor transactor, Locale locale) {
        this.transactor = transactor;
        this.locale = locale;
    }

    public List<String> getHistory(int page, User source) {
        return transactor.retrieve(session -> describe(getBySender(page, source)));
    }

    public List<String> getHistory(int page) {
        return transactor.retrieve(session -> describe(getAll(page)));
    }

    private List<Restriction> getBySender(int page, User user) {
        List<Restriction> restrictions = transactor.retrieve(session -> {
            Criterion userMustMatch;
            if (user != null) {
                userMustMatch = Restrictions.eq("source", user);
            } else {
                userMustMatch = Restrictions.isNull("source");
            }

            List<?> list = session.createCriteria(Restriction.class, "r")
                    .add(userMustMatch)
                    .addOrder(Order.desc("created"))
                    .setFirstResult(page * LIMIT)
                    .setMaxResults(LIMIT)
                    .list();

            return (List<Restriction>) list;
        });

        return restrictions;
    }

    private List<Restriction> getAll(int page) {
        List<Restriction> restrictions = transactor.retrieve(session -> {
            List<?> list = session.createCriteria(Restriction.class, "r")
                    .addOrder(Order.desc("created"))
                    .setFirstResult(page * LIMIT)
                    .setMaxResults(LIMIT)
                    .list();

            return (List<Restriction>) list;
        });

        return restrictions;
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
