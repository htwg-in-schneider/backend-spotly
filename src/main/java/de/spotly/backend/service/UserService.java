package de.spotly.backend.service;

import de.spotly.backend.entity.User;
import de.spotly.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminLogService adminLogService;

    // Benutzer sperren oder freigeben
    public void updateUserStatus(Long userId, boolean enabled, String adminName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

        user.setEnabled(enabled);
        userRepository.save(user);

        // Logging
        String action = enabled ? "USER_UNLOCKED" : "USER_LOCKED";
        String detail = "Benutzer '" + user.getUsername() + "' wurde " + (enabled ? "freigeben" : "gesperrt");
        adminLogService.log(adminName, action, detail);
    }

    // Benutzer löschen
    public void deleteUser(Long userId, String adminName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

        userRepository.deleteById(userId);

        // Logging
        adminLogService.log(adminName, "USER_DELETED", "Benutzer '" + user.getUsername() + "' wurde gelöscht.");
    }
}