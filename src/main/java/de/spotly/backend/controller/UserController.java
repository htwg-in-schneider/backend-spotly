package de.spotly.backend.controller;

import de.spotly.backend.entity.User;
import de.spotly.backend.repository.UserRepository;
import de.spotly.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    // Holt eine Liste aller registrierten User (für die Admin-Ansicht)
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Erlaubt es einem Nutzer, seinen eigenen Profilnamen zu ändern
    @PutMapping("/me")
    public ResponseEntity<?> updateMyProfile(@RequestBody User userUpdate, Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();

        // Identifiziert den User anhand seiner eindeutigen ID aus dem Login-Token
        String oauthId = principal.getName();

        return userRepository.findByOauthId(oauthId)
                .map(user -> {
                    if (userUpdate.getUsername() != null && !userUpdate.getUsername().isBlank()) {
                        user.setUsername(userUpdate.getUsername());
                        userRepository.save(user);
                        return ResponseEntity.ok(user);
                    } else {
                        return ResponseEntity.badRequest().body("Benutzername darf nicht leer sein.");
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Admin-Funktion: Sperrt oder entsperrt einen Benutzer
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User userDetails, Principal principal) {
        String adminName = (principal != null) ? principal.getName() : "Admin";
        userService.updateUserStatus(id, userDetails.isEnabled(), adminName);
        return ResponseEntity.ok().build();
    }

    // Admin-Funktion: Löscht einen Benutzer komplett aus dem System
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, Principal principal) {
        String adminName = (principal != null) ? principal.getName() : "Admin";
        userService.deleteUser(id, adminName);
        return ResponseEntity.ok().build();
    }
}