package org.maxgamer.maxbans.transactor;

import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Assert;
import org.hibernate.cfg.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.maxgamer.maxbans.orm.Ban;
import org.maxgamer.maxbans.orm.HibernateConfigurer;
import org.maxgamer.maxbans.repository.H2Test;
import org.maxgamer.maxbans.test.IntegrationTest;
import org.maxgamer.maxbans.transaction.TransactionLayer;
import org.maxgamer.maxbans.transaction.Transactor;

import java.time.Instant;
import java.util.UUID;

/**
 * @author netherfoam
 */
public class TransactorTest extends H2Test implements IntegrationTest {
    private Configuration hibernate;
    private SessionFactory sessionFactory;
    private Transactor transactor;

    @Before
    public void setup() {
        hibernate = HibernateConfigurer.configuration(getJdbc());
        sessionFactory = hibernate.buildSessionFactory();
        transactor = new Transactor(sessionFactory);
    }

    @After
    public void destroy() {
        sessionFactory.close();
    }

    @Test
    public void testSplitTransaction() {
        UUID id;

        try (TransactionLayer tx = transactor.transact()) {
            Ban ban = new Ban();
            ban.setCreated(Instant.EPOCH);
            tx.getEntityManager().persist(ban);

            id = ban.getId();
        }

        // Now we fetch the ban, it should be there
        try (TransactionLayer tx = transactor.transact()) {
            Ban ban = tx.getEntityManager().find(Ban.class, id);
            Assert.assertNotNull("Must retrieve ban", ban);
        }
    }

    @Test
    public void testSingleTransaction() {
        try (TransactionLayer tx = transactor.transact()) {
            Ban b = new Ban();
            b.setCreated(Instant.EPOCH);
            tx.getEntityManager().persist(b);

            Ban ban = tx.getEntityManager().find(Ban.class, b.getId());
            Assert.assertNotNull("Must retrieve ban", ban);
        }
    }

    @Test
    public void testNestedTransaction() {
        try (TransactionLayer t1 = transactor.transact()) {
            Ban b = new Ban();
            b.setCreated(Instant.EPOCH);
            t1.getEntityManager().persist(b);

            try (TransactionLayer t2 = transactor.transact()) {
                Ban ban = t2.getEntityManager().find(Ban.class, b.getId());
                Assert.assertNotNull("Must retrieve ban", ban);
            }

            t1.getEntityManager().flush();
        }
    }

    @Test
    public void testNoQueries() {
        try (TransactionLayer tx = transactor.transact()) {
            // Nothing
        }
    }
}
