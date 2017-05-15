package org.maxgamer.maxbans.transactor;

import junit.framework.Assert;
import org.hibernate.cfg.Configuration;
import org.junit.Test;
import org.maxgamer.maxbans.exception.TransactionException;
import org.maxgamer.maxbans.orm.Ban;
import org.maxgamer.maxbans.orm.HibernateConfigurer;
import org.maxgamer.maxbans.repository.H2Test;
import org.maxgamer.maxbans.test.IntegrationTest;
import org.maxgamer.maxbans.transaction.Transactor;

import java.time.Instant;
import java.util.UUID;

/**
 * @author netherfoam
 */
public class TransactorTest extends H2Test implements IntegrationTest {
    @Test
    public void testSplitTransaction() {
        Configuration hibernate = HibernateConfigurer.configuration(getJdbc());
        Transactor transactor = new Transactor(hibernate.buildSessionFactory());

        UUID id = transactor.retrieve(session -> {
            Ban ban = new Ban();
            ban.setCreated(Instant.EPOCH);
            session.persist(ban);

            return ban.getId();
        });

        // Now we fetch the ban, it should be there
        Ban ban = transactor.retrieve(session -> session.get(Ban.class, id));

        Assert.assertNotNull("Must retrieve ban", ban);
    }

    @Test
    public void testSingleTransaction() {
        Configuration hibernate = HibernateConfigurer.configuration(getJdbc());
        Transactor transactor = new Transactor(hibernate.buildSessionFactory());

        Ban ban = transactor.retrieve(session -> {
            Ban b = new Ban();
            b.setCreated(Instant.EPOCH);
            session.persist(b);

            return session.get(Ban.class, b.getId());
        });

        Assert.assertNotNull("Must retrieve ban", ban);
    }

    @Test
    public void testNestedTransaction() {
        Configuration hibernate = HibernateConfigurer.configuration(getJdbc());
        Transactor transactor = new Transactor(hibernate.buildSessionFactory());

        Ban ban = transactor.retrieve(session -> {
            Ban b = new Ban();
            b.setCreated(Instant.EPOCH);
            session.persist(b);

            return transactor.retrieve(s -> {
                return s.get(Ban.class, b.getId());
            });
        });

        Assert.assertNotNull("Must retrieve ban", ban);
    }

    @Test
    public void testNoQueries() {
        Configuration hibernate = HibernateConfigurer.configuration(getJdbc());
        Transactor transactor = new Transactor(hibernate.buildSessionFactory());

        transactor.work(session -> {
            // Nothing
        });
    }

    @Test
    public void testException() {
        Configuration hibernate = HibernateConfigurer.configuration(getJdbc());
        Transactor transactor = new Transactor(hibernate.buildSessionFactory());

        final String identifier = "WOOPS";
        try {
            transactor.work(session -> {
                throw new IllegalStateException(identifier);
            });
        } catch (TransactionException e) {
            Throwable inner = e.getCause();
            Assert.assertTrue("Expect inner to be IllegalStateException", inner instanceof IllegalStateException);
            Assert.assertEquals("Expect inner exception to be correct one", identifier, inner.getMessage());
        }
    }
}
