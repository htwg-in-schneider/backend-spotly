package de.spotly.backend.service;

import de.spotly.backend.entity.User;
import de.spotly.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// Dieser Service übernimmt die administrative Verwaltung der Benutzer
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminLogService adminLogService;

    /**
     * Sperrt oder entsperrt einen Benutzer und protokolliert diese Aktion.
     * @param userId Die ID des betroffenen Nutzers
     * @param enabled Der neue Status (true = aktiv, false = gesperrt)
     * @param adminName Name des Admins, der die Änderung vornimmt
     */
    public void updateUserStatus(Long userId, boolean enabled, String adminName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

        user.setEnabled(enabled);
        userRepository.save(user);

        // Protokollierung: Wir halten fest, ob gesperrt oder freigegeben wurde
        String action = enabled ? "USER_UNLOCKED" : "USER_LOCKED";
        String detail = "Benutzer '" + user.getUsername() + "' wurde " + (enabled ? "freigeben" : "gesperrt");
        adminLogService.log(adminName, action, detail);
    }

    /**
     * Löscht einen Benutzer endgültig aus der Datenbank.
     */
    public void deleteUser(Long userId, String adminName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

        userRepository.deleteById(userId);

        // Auch das endgültige Löschen wird für die Nachvollziehbarkeit geloggt
        adminLogService.log(adminName, "USER_DELETED", "Benutzer '" + user.getUsername() + "' wurde gelöscht.");
    }
}