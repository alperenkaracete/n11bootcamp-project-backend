package n11bootcamp_project_backend.log_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Hangi servis logladı
    @Column(nullable = false)
    private String serviceName;

    // Log seviyesi (INFO, ERROR, WARN)
    @Column(nullable = false)
    private String level;

    // Log mesajı
    @Column(nullable = false, length = 2000)
    private String message;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}