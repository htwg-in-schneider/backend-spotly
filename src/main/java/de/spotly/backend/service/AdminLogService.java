package de.spotly.backend.service;

import de.spotly.backend.entity.AdminLog;
import de.spotly.backend.repository.AdminLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AdminLogService {

    @Autowired
    private AdminLogRepository repository;

    public void log(String admin, String action, String details) {
        repository.save(new AdminLog(admin, action, details));
    }

    public List<AdminLog> getAllLogs() {
        return repository.findAllByOrderByTimestampDesc();
    }
}