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

    @OneToMany(mappedBy = "id.address", cascade = CascadeType.ALL)
    private List<UserAddress> users = new LinkedList<>();

    @ManyToMany
    @JoinTable(
            name = "Address_Ban",
            inverseJoinColumns = @JoinColumn(name = "ban_id"),
            joinColumns = @JoinColumn(name = "host")
    )
    private List<Ban> bans = new LinkedList<>();
    
    @ManyToMany
    @JoinTable(
            name = "Address_Mute",
            inverseJoinColumns = @JoinColumn(name = "mute_id"),
            joinColumns = @JoinColumn(name = "host")
    )
    private List<Mute> mutes = new LinkedList<>();

    public Address() {
        // Hibernate constructor
    }

    public Address(String host) {
        this.host = host;
    }

    public String getHost() {
        return host;
    }

    public List<UserAddress> getUsers () {
        return users;
    }

    public List<Ban> getBans() {
        return bans;
    }

    public List<Mute> getMutes() {
        return mutes;
    }
}
