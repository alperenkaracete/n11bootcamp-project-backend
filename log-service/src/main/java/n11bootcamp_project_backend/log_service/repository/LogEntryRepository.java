package n11bootcamp_project_backend.log_service.repository;

import n11bootcamp_project_backend.log_service.entity.LogEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LogEntryRepository extends JpaRepository<LogEntry, UUID> {

    // Servise göre logları getir
    List<LogEntry> findByServiceName(String serviceName);

    // Seviyeye göre logları getir
    List<LogEntry> findByLevel(String level);
}