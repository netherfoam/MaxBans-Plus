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
    private UUID id;

    @Column
    private String name;

    /**
     * Name except as lowercase
     */
    @Column
    private String alias;

    @Column(name = "first_active")
    private Instant firstActive;

    @Column(name = "last_active")
    private Instant lastActive;

    @ManyToMany
    @JoinTable(
            name = "Users_Mute",
            inverseJoinColumns = @JoinColumn(name = "mute_id"),
            joinColumns = @JoinColumn(name = "user_id")
    )
    private List<Mute> mutes = new LinkedList<>();

    @ManyToMany
    @JoinTable(
            name = "Users_Ban",
            inverseJoinColumns = @JoinColumn(name = "ban_id"),
            joinColumns = @JoinColumn(name = "user_id")
    )
    private List<Ban> bans = new LinkedList<>();

    @OneToMany(mappedBy = "id.user", cascade = CascadeType.ALL)
    @OrderBy("lastActive ASC")
    private List<UserAddress> addresses = new LinkedList<>();

    @OneToMany(mappedBy = "user")
    @OrderBy("expiresAt")
    private List<Warning> warnings = new LinkedList<>();

    public User() {
        // Hibernate constructor
    }

    public User(UUID id, String name) {
        this.firstActive = Instant.now();
        this.lastActive = firstActive;
        this.id = id;
        this.name = name;

        this.alias = name.toLowerCase();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.alias = name.toLowerCase();
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

    public List<UserAddress> getAddresses() {
        return addresses;
    }

    public List<Warning> getWarnings() {
        return warnings;
    }

    public Instant getFirstActive() {
        return firstActive;
    }

    public void setFirstActive(Instant firstActive) {
        this.firstActive = firstActive;
    }

    public String getAlias() {
        return alias;
    }

    public UserAddress getLastAddress() {
        if(addresses.isEmpty()) return null;

        return addresses.get(addresses.size() - 1);
    }
}
