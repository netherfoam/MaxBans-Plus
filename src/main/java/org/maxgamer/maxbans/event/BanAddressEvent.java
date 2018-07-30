package org.maxgamer.maxbans.event;

import org.maxgamer.maxbans.orm.Address;
import org.maxgamer.maxbans.orm.Ban;
import org.maxgamer.maxbans.orm.User;

public class BanAddressEvent extends MaxBansRestrictEvent<Address>  {
    private Ban ban;

    public BanAddressEvent(User admin, Address address, Ban ban) {
        super(admin, address);
        this.ban = ban;
    }

    public Ban getBan() {
        return ban;
    }
}
