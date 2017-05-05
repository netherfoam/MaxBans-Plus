package org.maxgamer.maxbans.orm;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Dirk Jamieson
 */
@Entity
@Table(name = "Address")
public class Address {
    @Id
    @Column
    private String host;

    @Column(name = "last_active")
    private long lastActive;

    @ManyToMany
    @JoinTable(name = "Address_Users", joinColumns = {
            @JoinColumn(name = "user_id")
    })
    private List<User> users = new LinkedList<>();

    @OneToOne
    private Ban ban;
    
    @OneToOne
    private Mute mute;

    private Address() {
        // Hibernate constructor
    }

    public Address(String host) {
        this.host = host;
    }

    public String getHost() {
        return host;
    }

    public List<User> getUsers () {
        return users;
    }

    public long getLastActive() {
        return lastActive;
    }

    public void setLastActive(long lastActive) {
        this.lastActive = lastActive;
    }

    public Ban getBan() {
        return ban;
    }

    public void setBan(Ban ban) {
        this.ban = ban;
    }

    public Mute getMute() {
        return mute;
    }

    public void setMute(Mute mute) {
        this.mute = mute;
    }
}
