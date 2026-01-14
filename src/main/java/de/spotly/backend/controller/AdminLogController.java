package de.spotly.backend.controller;

import de.spotly.backend.entity.AdminLog;
import de.spotly.backend.service.AdminLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Dieser Controller kümmert sich um die Abfrage der System-Protokolle (Logs)
@RestController
@RequestMapping("/api/admin/logs")
@CrossOrigin(origins = "*") // Erlaubt dem Frontend den Zugriff auf die Admin-Daten
public class AdminLogController {

    @Autowired
    private AdminLogService logService;

    // Holt einfach die ganze Liste der Logs aus der Datenbank ab
    @GetMapping
    public List<AdminLog> getLogs() {
        return logService.getAllLogs();
    }
}