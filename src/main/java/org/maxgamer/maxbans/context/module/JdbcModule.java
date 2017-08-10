package org.maxgamer.maxbans.context.module;

import dagger.Module;
import dagger.Provides;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.maxgamer.maxbans.config.JdbcConfig;
import org.maxgamer.maxbans.config.PluginConfig;
import org.maxgamer.maxbans.orm.HibernateConfigurer;

/**
 * @author netherfoam
 */
@Module
public class JdbcModule {
    @Provides
    public SessionFactory sessionFactory(Configuration configurer) {
        return configurer.buildSessionFactory();
    }

    @Provides
    public Configuration hibernateConfig(JdbcConfig jdbc) {
        return HibernateConfigurer.configuration(jdbc);
    }

    @Provides
    public JdbcConfig jdbcConfig(PluginConfig config) {
        return config.getJdbcConfig();
    }

}
