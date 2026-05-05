package dev.puanputri.portfolio.contact;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * JPA entity representing a contact form submission.
 */
@Entity
@Table(name = "contacts")
public class Contact extends PanacheEntity {

    @Column(name = "name", nullable = false, length = 100)
    public String name;

    @Column(name = "email", nullable = false, length = 254)
    public String email;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    public String message;

    @Column(name = "ip_address", length = 45)
    public String ipAddress;

    @Column(name = "created_at", nullable = false)
    public LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
