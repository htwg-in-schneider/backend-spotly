package de.spotly.backend.controller;

import de.spotly.backend.entity.User;
import de.spotly.backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Kümmert sich um die Profil-Daten des aktuell eingeloggten Nutzers
@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final UserRepository userRepository;

    public ProfileController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Holt das Profil passend zum Login-Token ab
    @GetMapping
    public ResponseEntity<User> getMyProfile(@AuthenticationPrincipal Jwt jwt) {
        // Die 'sub' ID ist die eindeutige Kennung von Auth0
        String oauthId = jwt.getSubject();

        // Sucht den User in unserer Datenbank; falls er neu ist, wird er automatisch angelegt
        User user = userRepository.findByOauthId(oauthId)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setOauthId(oauthId);
                    newUser.setRole("USER"); // Jeder neue Nutzer startet als normaler User
                    return userRepository.save(newUser);
                });

        return ResponseEntity.ok(user);
    }
}