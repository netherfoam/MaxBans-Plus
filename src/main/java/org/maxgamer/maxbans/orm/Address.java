package org.maxgamer.maxbans.orm;

import javax.persistence.*;
import java.util.ArrayList;
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

    @OneToMany
    @JoinTable(name = "Address_Ban", joinColumns = {
            @JoinColumn(name = "ban_id")
    })
    private List<Ban> bans = new LinkedList<>();
    
    @OneToMany
    @JoinTable(name = "Address_Mute", joinColumns = {
            @JoinColumn(name = "mute_id")
    })
    private List<Mute> mutes = new LinkedList<>();

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

    public List<Ban> getBans() {
        return bans;
    }

    public List<Mute> getMutes() {
        return mutes;
    }
}
