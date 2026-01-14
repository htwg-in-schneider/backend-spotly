package de.spotly.backend.repository;

import de.spotly.backend.entity.AdminLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AdminLogRepository extends JpaRepository<AdminLog, Long> {
    // Gibt die Logs absteigend sortiert zurück (neueste zuerst)
    List<AdminLog> findAllByOrderByTimestampDesc();
}