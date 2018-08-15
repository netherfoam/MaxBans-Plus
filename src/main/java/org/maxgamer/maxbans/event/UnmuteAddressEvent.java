package org.maxgamer.maxbans.event;

import org.maxgamer.maxbans.orm.Address;
import org.maxgamer.maxbans.orm.Mute;
import org.maxgamer.maxbans.orm.User;

public class UnmuteAddressEvent extends MaxBansRestrictEvent<Address> {
    private Mute mute;

    public UnmuteAddressEvent(User admin, Address target, Mute mute) {
        super(admin, target);
        this.mute = mute;
    }

    public Mute getMute() {
        return mute;
    }
}
