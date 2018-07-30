package org.maxgamer.maxbans.event;

import org.maxgamer.maxbans.orm.Ban;
import org.maxgamer.maxbans.orm.User;

public class BanUserEvent extends MaxBansRestrictEvent<User>  {
    private Ban ban;

    public BanUserEvent(User admin, User user, Ban ban) {
        super(admin, user);
        this.ban = ban;
    }

    public Ban getBan() {
        return ban;
    }
}
