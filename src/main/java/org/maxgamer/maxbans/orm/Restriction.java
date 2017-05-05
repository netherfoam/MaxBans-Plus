package org.maxgamer.maxbans.orm;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * @author Dirk Jamieson
 */
@MappedSuperclass
public abstract class Restriction {
    @Id
    //@GeneratedValue
    private UUID id = UUID.randomUUID();
    
    @Column
    protected Instant created;
    
    @Column(name = "expires_at")
    protected Instant expiresAt;
    
    @Column
    protected String reason;
    
    @ManyToOne
    protected User source;

    public UUID getId() {
        return id;
    }

    public Instant getCreated() {
        return created;
    }

    public Restriction setCreated(Instant created) {
        this.created = created;
        return this;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Restriction setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
        return this;
    }

    public String getReason() {
        return reason;
    }

    public Restriction setReason(String reason) {
        this.reason = reason;
        return this;
    }

    public User getSource() {
        return source;
    }

    public Restriction setSource(User source) {
        this.source = source;
        return this;
    }
}
