package n11bootcamp_project_backend.user_service.services;

import n11bootcamp_project_backend.user_service.dto.request.LoginRequest;
import n11bootcamp_project_backend.user_service.dto.request.RegisterRequest;
import n11bootcamp_project_backend.user_service.dto.response.TokenResponse;

public interface AuthService {
    // Yeni kullanıcı kaydı
    void register(RegisterRequest request);

    // Kullanıcı girişi, token döner
    TokenResponse login(LoginRequest request);

    // Çıkış işlemi, token Redis'ten silinir
    void logout(String token);

    // Access token süresi dolunca yeniler
    TokenResponse refreshToken(String refreshToken);
}
