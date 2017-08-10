package org.maxgamer.maxbans.context.component;

import dagger.Subcomponent;
import org.maxgamer.maxbans.listener.RestrictionListener;

/**
 * @author netherfoam
 */
@Subcomponent
public interface ListenerComponent {
    RestrictionListener restriction();
}
