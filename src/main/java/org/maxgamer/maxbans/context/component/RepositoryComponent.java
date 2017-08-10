package org.maxgamer.maxbans.context.component;

import dagger.Subcomponent;
import org.maxgamer.maxbans.repository.*;

/**
 * @author netherfoam
 */
@Subcomponent
public interface RepositoryComponent {
    AddressRepository address();
    BanRepository ban();
    MuteRepository mute();
    UserRepository user();
    WarningRepository warn();
}
