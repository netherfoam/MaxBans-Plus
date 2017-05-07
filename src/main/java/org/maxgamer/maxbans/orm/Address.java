package org.maxgamer.maxbans.orm;

import com.avaje.ebean.annotation.Where;

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

    @OneToOne
    @Where(clause = "expiresAt > now() OR expiresAt IS NULL")
    private Ban ban;
    
    @OneToOne
    @Where(clause = "expiresAt > now() OR expiresAt IS NULL")
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

    public List<UserAddress> getUsers () {
        return users;
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
