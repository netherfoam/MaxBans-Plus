package org.maxgamer.maxbans.context.module;

import dagger.Module;
import dagger.Provides;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.maxgamer.maxbans.config.JdbcConfig;
import org.maxgamer.maxbans.config.PluginConfig;
import org.maxgamer.maxbans.orm.HibernateConfigurer;

import javax.inject.Singleton;

/**
 * @author netherfoam
 */
@Module
public class JdbcModule {
    @Provides
    @Singleton
    public SessionFactory sessionFactory(Configuration configurer, PluginModule plugin) {
        SessionFactory factory = configurer.buildSessionFactory();
        plugin.setSessionInitialised(true);

        return factory;
    }

    @Provides
    @Singleton
    public Configuration hibernateConfig(JdbcConfig jdbc) {
        return HibernateConfigurer.configuration(jdbc);
    }

    @Provides
    @Singleton
    public JdbcConfig jdbcConfig(PluginConfig config) {
        return config.getJdbcConfig();
    }
}
