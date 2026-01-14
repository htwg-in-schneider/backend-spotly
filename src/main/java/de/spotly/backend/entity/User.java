package de.spotly.backend.entity;

import jakarta.persistence.*;

// Repräsentiert einen Benutzer in der Datenbank (Tabelle "app_user")
@Entity
@Table(name = "app_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Interne Datenbank-ID

    private String oauthId; // Die eindeutige ID vom Login-Provider (Auth0)
    private String username; // Der Anzeigename in der App
    private String email;
    private String role; // Rolle für Berechtigungen (z. B. "USER" oder "ADMIN")

    // Gibt an, ob der Account aktiv ist oder vom Admin gesperrt wurde
    private boolean enabled = true;

    // Standard-Konstruktor für JPA
    public User() {
    }

    // Konstruktor zum schnellen Anlegen eines neuen Users
    public User(String username, String email, String role, String oauthId) {
        this.username = username;
        this.email = email;
        this.role = role;
        this.oauthId = oauthId;
    }

    // Getter und Setter für den Zugriff auf die Profildaten
    public String getOauthId() {
        return oauthId;
    }

    public void setOauthId(String oauthId) {
        this.oauthId = oauthId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}