package de.spotly.backend.controller;

import de.spotly.backend.entity.AdminLog;
import de.spotly.backend.service.AdminLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/logs")
@CrossOrigin(origins = "*") // Erlaube Zugriff vom Frontend
public class AdminLogController {

    @Autowired
    private AdminLogService logService;

    @GetMapping
    public List<AdminLog> getLogs() {
        return logService.getAllLogs();
    }
}