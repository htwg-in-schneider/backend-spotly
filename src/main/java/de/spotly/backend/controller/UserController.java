package de.spotly.backend.controller;

import de.spotly.backend.entity.User;
import de.spotly.backend.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
// Erlaubt Anfragen von deinem Vue.js Frontend (Standardport 5173 oder 8080)
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:8080"})
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 1. Alle Nutzer anzeigen (Anforderung b: Administrator kann Stammdaten sehen)
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 2. Neuen Nutzer erstellen (Hilfreich für Tests und Initialisierung)
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        User savedUser = userRepository.save(user);
        return ResponseEntity.status(201).body(savedUser);
    }

    // 3. Nutzer bearbeiten / Rolle ändern (Anforderung b: Administrator kann Stammdaten verändern)
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody User userDetails) {
        return userRepository.findById(id).map(user -> {
            user.setUsername(userDetails.getUsername());
            user.setRole(userDetails.getRole());
            User updatedUser = userRepository.save(user);
            return ResponseEntity.ok(updatedUser);
        }).orElse(ResponseEntity.notFound().build());
    }
    @PutMapping("/me") // Die URL ist dann /api/users/me (oder /api/profile/me)
    public ResponseEntity<User> updateMyProfile(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody Map<String, String> updates) {

        // 1. Auth0-ID aus dem Token holen
        String oauthId = jwt.getSubject();
        String newUsername = updates.get("username");

        // 2. User in der DB anhand der oauthId suchen
        return userRepository.findByOauthId(oauthId).map(user -> {
            user.setUsername(newUsername);
            User savedUser = userRepository.save(user);
            return ResponseEntity.ok(savedUser);
        }).orElse(ResponseEntity.notFound().build());
    }

    // Optional: Nutzer löschen (Falls du Admins volle Kontrolle geben willst)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}