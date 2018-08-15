package org.maxgamer.maxbans.event;

import org.maxgamer.maxbans.orm.Address;
import org.maxgamer.maxbans.orm.Ban;
import org.maxgamer.maxbans.orm.User;

public class UnbanAddressEvent extends MaxBansRestrictEvent<Address> {
    private Ban ban;

    public UnbanAddressEvent(User admin, Address target, Ban ban) {
        super(admin, target);
        this.ban = ban;
    }

    public Ban getBan() {
        return ban;
    }
}
