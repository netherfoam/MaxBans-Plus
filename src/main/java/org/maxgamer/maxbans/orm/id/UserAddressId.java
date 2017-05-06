package org.maxgamer.maxbans.orm.id;

import org.maxgamer.maxbans.orm.Address;
import org.maxgamer.maxbans.orm.User;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

/**
 * @author netherfoam
 */
@Embeddable
public class UserAddressId implements Serializable {
    @ManyToOne(optional = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "address")
    private Address address;

    private UserAddressId() {
        // Hibernate constructor
    }

    public UserAddressId(User user, Address address) {
        this.user = user;
        this.address = address;
    }

    public User getUser() {
        return user;
    }

    public Address getAddress() {
        return address;
    }
}
