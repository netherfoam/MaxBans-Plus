package org.maxgamer.maxbans.context.component;

import dagger.Component;
import org.hibernate.SessionFactory;
import org.maxgamer.maxbans.MaxBansPlus;
import org.maxgamer.maxbans.context.module.JdbcModule;
import org.maxgamer.maxbans.context.module.PluginModule;
import org.maxgamer.maxbans.context.module.ServiceModule;
import org.maxgamer.maxbans.transaction.Transactor;

import javax.inject.Singleton;
import java.util.logging.Logger;

/**
 * @author netherfoam
 */
@Component(modules = {
        PluginModule.class,
        JdbcModule.class,
        ServiceModule.class
})
@Singleton
public interface PluginComponent {
    MaxBansPlus plugin();
    Transactor transactor();
    RepositoryComponent repositories();
    ServiceComponent services();
    ListenerComponent listeners();
    CommandExecutorComponent commands();
    Logger logger();
    SessionFactory sessionFactory();
}
