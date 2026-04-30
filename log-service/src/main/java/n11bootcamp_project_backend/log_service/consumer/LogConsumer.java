package n11bootcamp_project_backend.log_service.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import n11bootcamp_project_backend.log_service.dto.LogMessage;
import n11bootcamp_project_backend.log_service.entity.LogEntry;
import n11bootcamp_project_backend.log_service.repository.LogEntryRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LogConsumer {

    private final LogEntryRepository logEntryRepository;

    @RabbitListener(queues = "log.queue")
    public void handleLogEvent(LogMessage logMessage) {
        log.info("Log event received from: {}", logMessage.getServiceName());

        LogEntry logEntry = LogEntry.builder()
                .serviceName(logMessage.getServiceName())
                .level(logMessage.getLevel())
                .message(logMessage.getMessage())
                .build();

        logEntryRepository.save(logEntry);
    }
}