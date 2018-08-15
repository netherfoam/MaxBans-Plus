package org.maxgamer.maxbans.event;

import org.maxgamer.maxbans.orm.Mute;
import org.maxgamer.maxbans.orm.User;

public class MuteUserEvent extends MaxBansRestrictEvent<User> {
    private Mute mute;

    public MuteUserEvent(User admin, User user, Mute mute) {
        super(admin, user);
        this.mute = mute;
    }

    public Mute getMute() {
        return mute;
    }
}
