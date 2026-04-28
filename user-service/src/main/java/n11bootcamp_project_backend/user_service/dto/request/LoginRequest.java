package n11bootcamp_project_backend.user_service.dto.request;

public record LoginRequest(
        String email,
        String password
) {}
