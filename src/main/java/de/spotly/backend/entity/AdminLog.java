package de.spotly.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "admin_logs")
public class AdminLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime timestamp;
    private String adminName;
    private String action;
    private String details;

    public AdminLog() {}

    public AdminLog(String adminName, String action, String details) {
        this.timestamp = LocalDateTime.now();
        this.adminName = adminName;
        this.action = action;
        this.details = details;
    }

    public Long getId() { return id; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getAdminName() { return adminName; }
    public String getAction() { return action; }
    public String getDetails() { return details; }
}