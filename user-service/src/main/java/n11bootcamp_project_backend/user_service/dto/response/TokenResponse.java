package n11bootcamp_project_backend.user_service.dto.response;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        long expiresIn
) {}
