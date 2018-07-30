package org.maxgamer.maxbans.event;

import org.maxgamer.maxbans.orm.Address;
import org.maxgamer.maxbans.orm.Ban;
import org.maxgamer.maxbans.orm.Mute;
import org.maxgamer.maxbans.orm.User;

public class MuteAddressEvent extends MaxBansRestrictEvent<Address> {
    private Mute mute;

    public MuteAddressEvent(User admin, Address address, Mute mute) {
        super(admin, address);
        this.mute = mute;
    }

    public Mute getMute() {
        return mute;
    }
}
