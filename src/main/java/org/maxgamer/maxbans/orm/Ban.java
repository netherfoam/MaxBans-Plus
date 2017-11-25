package org.maxgamer.maxbans.orm;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import java.util.Collections;
import java.util.List;

/**
 * @author Dirk Jamieson
 */
@Entity
public class Ban extends Restriction {
    @ManyToMany
    @JoinTable(
            name = "Users_Ban",
            inverseJoinColumns = @JoinColumn(name = "user_id"),
            joinColumns = @JoinColumn(name = "ban_id")
    )
    private List<User> users = Collections.emptyList();

    @ManyToMany
    @JoinTable(
            name = "Address_Ban",
            inverseJoinColumns = @JoinColumn(name = "host"),
            joinColumns = @JoinColumn(name = "ban_id")
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
