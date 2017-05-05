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

    @JoinTable(name = "Users_Mute", 
            joinColumns = @JoinColumn(name = "mute_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @OneToMany
    private List<Mute> mutes = new LinkedList<>();

    @JoinTable(name = "Users_Ban", joinColumns = {
            @JoinColumn(name = "ban_id")
    })
    @OneToMany
    private List<Ban> bans = new LinkedList<>();

    @JoinTable(name = "Address_Users", joinColumns = {
            @JoinColumn(name = "address")
    })
    @ManyToMany(cascade = CascadeType.ALL)
    private List<Address> addresses = new LinkedList<>();

    private User() {}

    public User(UUID id, String name) {
        this();

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

    public List<Mute> getMutes() {
        return mutes;
    }

    public List<Ban> getBans() {
        return bans;
    }

    public List<Address> getAddresses() {
        return addresses;
    }
}
