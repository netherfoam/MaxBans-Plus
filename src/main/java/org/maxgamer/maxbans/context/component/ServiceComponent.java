package org.maxgamer.maxbans.context.component;

import dagger.Subcomponent;
import org.maxgamer.maxbans.service.*;
import org.maxgamer.maxbans.service.metric.MetricService;

/**
 * @author netherfoam
 */
@Subcomponent
public interface ServiceComponent {
    UserService user();
    AddressService address();
    BroadcastService broadcast();
    LocatorService locator();
    LockdownService lockdown();
    WarningService warn();
    MetricService metric();
    GeoIPService geoIP();
}
