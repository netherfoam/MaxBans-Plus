package org.maxgamer.maxbans.orm;

import com.avaje.ebean.annotation.Where;

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

    @Column(name = "first_active")
    private Instant firstActive;

    @Column(name = "last_active")
    private Instant lastActive;

    @OneToOne
    @Where(clause = "expiresAt > now() OR expiresAt IS NULL")
    private Mute mute;

    @OneToOne
    @Where(clause = "expiresAt > now() OR expiresAt IS NULL")
    private Ban ban;

    @OneToMany(mappedBy = "id.user", cascade = CascadeType.ALL)
    @OrderBy("lastActive ASC")
    private List<UserAddress> addresses = new LinkedList<>();

    @OneToMany(mappedBy = "user")
    @OrderBy("expiresAt")
    @Where(clause = "expiresAt > now()")
    private List<Warning> warnings = new LinkedList<>();

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
}
