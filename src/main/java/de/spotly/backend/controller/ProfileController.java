package de.spotly.backend.controller;

import de.spotly.backend.entity.User;
import de.spotly.backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final UserRepository userRepository;

    public ProfileController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<User> getMyProfile(@AuthenticationPrincipal Jwt jwt) {
        String oauthId = jwt.getSubject(); // Holt die 'sub' ID aus dem Token

        // Suche den User in der DB. Wenn nicht da, lege ihn evtl. neu an (Self-Registration)
        User user = userRepository.findByOauthId(oauthId)
                .orElseGet(() -> {
                    // Optional: User automatisch erstellen, wenn er das erste Mal kommt
                    User newUser = new User();
                    newUser.setOauthId(oauthId);
                    newUser.setRole("USER"); // Standardrolle
                    return userRepository.save(newUser);
                });

        return ResponseEntity.ok(user);
    }
}