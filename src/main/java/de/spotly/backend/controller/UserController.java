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

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Endpunkt für das eigene Profil - nutzt findByOauthId
    @PutMapping("/me")
    public ResponseEntity<?> updateMyProfile(@RequestBody User userUpdate, Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();

        // principal.getName() liefert bei Auth0 standardmäßig die OauthId (den "sub" claim)
        String oauthId = principal.getName();

        return userRepository.findByOauthId(oauthId)
                .map(user -> {
                    // Wir aktualisieren nur den Benutzernamen
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

    // WICHTIG: Diese Route muss UNTER /me stehen, damit "me" nicht als ID interpretiert wird
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User userDetails, Principal principal) {
        String adminName = (principal != null) ? principal.getName() : "Admin";
        userService.updateUserStatus(id, userDetails.isEnabled(), adminName);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, Principal principal) {
        String adminName = (principal != null) ? principal.getName() : "Admin";
        userService.deleteUser(id, adminName);
        return ResponseEntity.ok().build();
    }
}