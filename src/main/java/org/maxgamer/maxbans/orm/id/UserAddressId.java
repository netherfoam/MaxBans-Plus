package org.maxgamer.maxbans.orm.id;

import org.maxgamer.maxbans.orm.Address;
import org.maxgamer.maxbans.orm.User;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Objects;

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

    public UserAddressId() {
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

    @Override
    public int hashCode() {
        return Objects.hash(user, address);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final UserAddressId other = (UserAddressId) obj;

        return Objects.equals(this.user.getId(), other.user.getId())
                && Objects.equals(this.address.getHost(), other.address.getHost());
    }
}
