package dev.puanputri.portfolio.event;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * JPA entity representing an analytics page event (page view or project click).
 */
@Entity
@Table(name = "page_events")
public class PageEvent extends PanacheEntity {

    /**
     * The type of analytics event.
     */
    public enum EventType {
        PAGE_VIEW,
        PROJECT_CLICK
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 30)
    public EventType eventType;

    @Column(name = "page", nullable = false, length = 512)
    public String page;

    @Column(name = "referrer", length = 1024)
    public String referrer;

    @Column(name = "user_agent", length = 512)
    public String userAgent;

    @Column(name = "created_at", nullable = false)
    public LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
