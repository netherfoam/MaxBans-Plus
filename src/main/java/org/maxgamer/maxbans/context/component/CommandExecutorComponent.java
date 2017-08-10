package org.maxgamer.maxbans.context.component;

import dagger.Subcomponent;
import org.maxgamer.maxbans.command.*;

/**
 * @author netherfoam
 */
@Subcomponent
public interface CommandExecutorComponent {
    BanCommandExecutor ban();
    HistoryCommandExecutor history();
    IPBanCommandExecutor ipban();
    IPMuteCommandExecutor ipmute();
    KickCommandExecutor kick();
    LockdownCommandExecutor lockdown();
    LookupCommandExecutor lookup();
    MuteCommandExecutor mute();
    UnbanCommandExecutor unban();
    UnmuteCommandExecutor unmute();
    WarnCommandExecutor warn();
}
