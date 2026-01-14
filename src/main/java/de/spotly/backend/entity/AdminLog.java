package de.spotly.backend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

// Diese Klasse sorgt dafür, dass die Logs in der Datenbanktabelle "admin_logs" landen
@Entity
@Table(name = "admin_logs")
public class AdminLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Eindeutige Nummer für jeden Log-Eintrag

    private LocalDateTime timestamp; // Wann die Aktion passiert ist
    private String adminName;        // Welcher Admin (oder das System) es war
    private String action;           // Was gemacht wurde (z.B. USER_LOCKED)
    private String details;          // Genauere Infos zur Aktion

    // Standard-Konstruktor (wird von JPA benötigt)
    public AdminLog() {
    }

    // Konstruktor, um schnell einen neuen Log-Eintrag mit der aktuellen Zeit zu erstellen
    public AdminLog(String adminName, String action, String details) {
        this.timestamp = LocalDateTime.now();
        this.adminName = adminName;
        this.action = action;
        this.details = details;
    }

    // Die Getter-Methoden, um die Daten später wieder auszulesen
    public Long getId() {
        return id;
    }

    public LocalDateTime getTimestamp() {
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