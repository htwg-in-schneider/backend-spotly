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

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User userDetails, Principal principal) {
        String adminName = (principal != null) ? principal.getName() : "Admin";

        // Geändert von getEnabled() auf isEnabled()
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