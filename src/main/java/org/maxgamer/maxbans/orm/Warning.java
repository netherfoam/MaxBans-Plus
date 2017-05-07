package org.maxgamer.maxbans.orm;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.time.Instant;

/**
 * @author netherfoam
 */
@Entity
public class Warning extends Restriction {
    @ManyToOne(optional = false)
    private User user;

    private Warning() {
        // Hibernate constructor
    }

    public Warning(User user) {
        this.user = user;
        this.created = Instant.now();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
