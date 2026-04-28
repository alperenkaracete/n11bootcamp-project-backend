package n11bootcamp_project_backend.user_service.dto.request;

public record RegisterRequest(
        String firstName,
        String lastName,
        String email,
        String password
) {}