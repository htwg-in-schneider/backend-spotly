package de.spotly.backend.entity;

import jakarta.persistence.*;
import java.time.Instant; // Wichtig: Wir nutzen jetzt Instant

@Entity
@Table(name = "admin_logs")
public class AdminLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Instant sorgt für einen eindeutigen Zeitpunkt auf der Weltachse (UTC)
    private Instant timestamp;

    private String adminName;
    private String action;
    private String details;

    public AdminLog() {
    }

    // Im Konstruktor nutzen wir Instant.now()
    public AdminLog(String adminName, String action, String details) {
        this.timestamp = Instant.now(); // Erzeugt den Zeitstempel in UTC
        this.adminName = adminName;
        this.action = action;
        this.details = details;
    }

    public Long getId() {
        return id;
    }

    // Der Getter gibt nun ein Instant zurück
    public Instant getTimestamp() {
        return timestamp;
    }

    public String getAdminName() {
        return adminName;
    }

    public String getAction() {
        return action;
    }

    public String getDetails() {
        return details;
    }
}