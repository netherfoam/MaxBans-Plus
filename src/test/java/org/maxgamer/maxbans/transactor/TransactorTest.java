package org.maxgamer.maxbans.transactor;

import junit.framework.Assert;
import org.hibernate.cfg.Configuration;
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
    @Test
    public void testSplitTransaction() {
        Configuration hibernate = HibernateConfigurer.configuration(getJdbc());
        Transactor transactor = new Transactor(hibernate.buildSessionFactory());

        UUID id;

        try (TransactionLayer tx = transactor.transact()) {
            Ban ban = new Ban();
            ban.setCreated(Instant.EPOCH);
            tx.getSession().persist(ban);

            id = ban.getId();
        }

        // Now we fetch the ban, it should be there
        try (TransactionLayer tx = transactor.transact()) {
            Ban ban = tx.getSession().get(Ban.class, id);
            Assert.assertNotNull("Must retrieve ban", ban);
        }
    }

    @Test
    public void testSingleTransaction() {
        Configuration hibernate = HibernateConfigurer.configuration(getJdbc());
        Transactor transactor = new Transactor(hibernate.buildSessionFactory());

        try (TransactionLayer tx = transactor.transact()) {
            Ban b = new Ban();
            b.setCreated(Instant.EPOCH);
            tx.getSession().persist(b);

            Ban ban = tx.getSession().get(Ban.class, b.getId());
            Assert.assertNotNull("Must retrieve ban", ban);
        }
    }

    @Test
    public void testNestedTransaction() {
        Configuration hibernate = HibernateConfigurer.configuration(getJdbc());
        Transactor transactor = new Transactor(hibernate.buildSessionFactory());

        try (TransactionLayer t1 = transactor.transact()) {
            Ban b = new Ban();
            b.setCreated(Instant.EPOCH);
            t1.getSession().persist(b);

            try (TransactionLayer t2 = transactor.transact()) {
                Ban ban = t2.getSession().get(Ban.class, b.getId());
                Assert.assertNotNull("Must retrieve ban", ban);
            }
        }
    }

    @Test
    public void testNoQueries() {
        Configuration hibernate = HibernateConfigurer.configuration(getJdbc());
        Transactor transactor = new Transactor(hibernate.buildSessionFactory());

        try (TransactionLayer tx = transactor.transact()) {
            // Nothing
        }
    }
}
