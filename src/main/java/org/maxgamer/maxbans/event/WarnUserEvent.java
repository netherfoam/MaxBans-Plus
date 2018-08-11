package org.maxgamer.maxbans.event;

import org.maxgamer.maxbans.orm.User;
import org.maxgamer.maxbans.orm.Warning;

public class WarnUserEvent extends MaxBansRestrictEvent<User> {
    private Warning warning;

    public WarnUserEvent(User admin, User user, Warning warning) {
        super(admin, user);
        this.warning = warning;
    }

    public Warning getWarning() {
        return warning;
    }
}
