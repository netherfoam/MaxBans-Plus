package org.maxgamer.maxbans.event;

import org.maxgamer.maxbans.orm.Mute;
import org.maxgamer.maxbans.orm.User;

public class UnmuteUserEvent extends MaxBansRestrictEvent<User> {
    private Mute mute;

    public UnmuteUserEvent(User admin, User target, Mute mute) {
        super(admin, target);
        this.mute = mute;
    }

    public Mute getMute() {
        return mute;
    }
}
