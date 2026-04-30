package n11bootcamp_project_backend.log_service.controller;

import lombok.RequiredArgsConstructor;
import n11bootcamp_project_backend.log_service.entity.LogEntry;
import n11bootcamp_project_backend.log_service.repository.LogEntryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class LogController {

    private final LogEntryRepository logEntryRepository;

    // Tüm logları getir
    @GetMapping
    public ResponseEntity<List<LogEntry>> getAllLogs() {
        return ResponseEntity.ok(logEntryRepository.findAll());
    }

    // Servise göre logları getir
    @GetMapping("/service/{serviceName}")
    public ResponseEntity<List<LogEntry>> getLogsByService(
            @PathVariable String serviceName) {
        return ResponseEntity.ok(logEntryRepository.findByServiceName(serviceName));
    }

    // Seviyeye göre logları getir
    @GetMapping("/level/{level}")
    public ResponseEntity<List<LogEntry>> getLogsByLevel(
            @PathVariable String level) {
        return ResponseEntity.ok(logEntryRepository.findByLevel(level));
    }
}