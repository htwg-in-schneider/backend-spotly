package de.spotly.backend.service;

import de.spotly.backend.entity.AdminLog;
import de.spotly.backend.repository.AdminLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// Dieser Service kümmert sich um das Speichern und Abrufen der Admin-Aktionen
@Service
public class AdminLogService {

    @Autowired
    private AdminLogRepository repository;

    // Erstellt einen neuen Log-Eintrag und speichert ihn in der Datenbank
    public void log(String admin, String action, String details) {
        repository.save(new AdminLog(admin, action, details));
    }

    // Holt alle Logs ab, sortiert nach der Zeit (neueste zuerst)
    public List<AdminLog> getAllLogs() {
        return repository.findAllByOrderByTimestampDesc();
    }
}