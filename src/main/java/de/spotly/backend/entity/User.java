package de.spotly.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "app_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nutzername darf nicht leer sein")
    private String username;

    @Email(message = "Ungültiges E-Mail-Format")
    @NotBlank(message = "E-Mail darf nicht leer sein")
    private String email; // Neues Feld für die E-Mail

    @NotBlank(message = "Rolle darf nicht leer sein")
    private String role;

    public User() {}

    public User(String username, String email, String role) {
        this.username = username;
        this.email = email;
        this.role = role;
    }

    // Getter und Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}