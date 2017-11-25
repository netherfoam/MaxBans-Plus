package org.maxgamer.maxbans.orm;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;

/**
 * @author Dirk Jamieson
 */
@Entity
public class Mute extends Restriction {
    @ManyToMany
    @JoinTable(
            name = "Users_Mute",
            inverseJoinColumns = @JoinColumn(name = "user_id"),
            joinColumns = @JoinColumn(name = "mute_id")
    )
    private List<User> users = Collections.emptyList();

    @ManyToMany
    @JoinTable(
            name = "Address_Mute",
            inverseJoinColumns = @JoinColumn(name = "host"),
            joinColumns = @JoinColumn(name = "mute_id")
    )
    private List<Address> addresses = Collections.emptyList();

    @Override
    public Tenant getTenant() {
        if (!users.isEmpty()) {
            return users.get(0);
        }

        if (!addresses.isEmpty()) {
            return addresses.get(0);
        }

        return null;
    }
}
