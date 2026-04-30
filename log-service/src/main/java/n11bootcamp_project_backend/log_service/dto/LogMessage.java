package n11bootcamp_project_backend.log_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogMessage {
    private String serviceName;
    private String level;
    private String message;
}