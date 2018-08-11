package org.maxgamer.maxbans.event;

import org.maxgamer.maxbans.orm.Ban;
import org.maxgamer.maxbans.orm.User;

public class UnbanUserEvent extends MaxBansRestrictEvent<User> {
    private Ban ban;

    public UnbanUserEvent(User admin, User target, Ban ban) {
        super(admin, target);
        this.ban = ban;
    }

    public Ban getBan() {
        return ban;
    }
}
