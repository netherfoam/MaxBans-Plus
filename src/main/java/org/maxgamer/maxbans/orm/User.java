package org.maxgamer.maxbans.orm;

import javax.persistence.*;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * @author Dirk Jamieson
 */
@Entity
@Table(name = "Users")
public class User {
    @Id
    @Column
    private UUID id;

    @Column
    private String name;

    @Column(name = "last_active")
    private Instant lastActive;

    @OneToOne
    private Mute mute;

    @OneToOne
    private Ban ban;

    @JoinTable(name = "Address_Users", joinColumns = {
            @JoinColumn(name = "address")
    })
    @ManyToMany(cascade = CascadeType.ALL)
    private List<Address> addresses = new LinkedList<>();

    private User() {
        // Hibernate constructor
    }

    public User(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Instant getLastActive() {
        return lastActive;
    }

    public void setLastActive(Instant lastActive) {
        this.lastActive = lastActive;
    }

    public Mute getMute() {
        return mute;
    }

    public void setMute(Mute mute) {
        this.mute = mute;
    }

    public Ban getBan() {
        return ban;
    }

    public void setBan(Ban ban) {
        this.ban = ban;
    }

    public List<Address> getAddresses() {
        return addresses;
    }
}
