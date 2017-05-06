package org.maxgamer.maxbans.orm;

import org.maxgamer.maxbans.orm.id.UserAddressId;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.Instant;

/**
 * @author netherfoam
 */
@Entity
@Table(name = "Address_User")
public class UserAddress {
    @EmbeddedId
    private UserAddressId id;

    @Column(name = "first_active")
    private Instant firstActive;

    @Column(name = "last_active")
    private Instant lastActive;

    private UserAddress() {
        // Hibernate constructor
    }

    public UserAddress(User user, Address address) {
        this(new UserAddressId(user, address));
    }

    public UserAddress(UserAddressId id) {
        this.id = id;
        this.firstActive = Instant.now();
        this.lastActive = Instant.now();
    }

    public User getUser() {
        return id.getUser();
    }

    public Address getAddress() {
        return id.getAddress();
    }

    public Instant getFirstActive() {
        return firstActive;
    }

    public void setFirstActive(Instant firstActive) {
        this.firstActive = firstActive;
    }

    public Instant getLastActive() {
        return lastActive;
    }

    public void setLastActive(Instant lastActive) {
        this.lastActive = lastActive;
    }
}
