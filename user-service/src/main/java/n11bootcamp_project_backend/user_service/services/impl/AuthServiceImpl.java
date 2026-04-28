package n11bootcamp_project_backend.user_service.services.impl;

import lombok.RequiredArgsConstructor;
import n11bootcamp_project_backend.user_service.util.JwtUtil;
import n11bootcamp_project_backend.user_service.dto.request.LoginRequest;
import n11bootcamp_project_backend.user_service.dto.request.RegisterRequest;
import n11bootcamp_project_backend.user_service.dto.response.TokenResponse;
import n11bootcamp_project_backend.user_service.entity.User;
import n11bootcamp_project_backend.user_service.enums.Role;
import n11bootcamp_project_backend.user_service.repository.UserRepository;
import n11bootcamp_project_backend.user_service.services.AuthService;
import n11bootcamp_project_backend.user_service.services.RedisService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor  //Lombok sayesinde constructor injectiona gerek kalmadı.
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final RedisService redisService;

    @Override
    public void register(RegisterRequest request) {
        // Email başka biri tarafından alınmış mı?
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already exists");
        }

        // Kullanıcıyı oluştur ve kaydet
        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .firstName(request.firstName())
                .lastName(request.lastName())
                .role(Role.CUSTOMER)
                .build();

        userRepository.save(user);
    }

    @Override
    public TokenResponse login(LoginRequest request) {
        // Kullanıcı var mı?
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Şifre doğru mu?
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // Token üret
        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        // Access token'ı Redis'e kaydet (1 saat)
        redisService.saveToken("auth:token:" + user.getId(), accessToken, 3600);

        return new TokenResponse(accessToken, refreshToken, 3600);
    }

    @Override
    public void logout(String token) {
        // Token'dan userId çıkar
        UUID userId = jwtUtil.extractUserId(token);

        // Redis'ten token'ı sil
        redisService.deleteToken("auth:token:" + userId);
    }

    @Override
    public TokenResponse refreshToken(String refreshToken) {
        // Refresh token geçerli mi?
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        // Token'dan userId çıkar
        UUID userId = jwtUtil.extractUserId(refreshToken);

        // Kullanıcıyı DB'den çek
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Yeni access token üret
        String newAccessToken = jwtUtil.generateAccessToken(user.getId(), user.getRole());

        // Redis'i güncelle
        redisService.saveToken("auth:token:" + userId, newAccessToken, 3600);

        return new TokenResponse(newAccessToken, refreshToken, 3600);
    }
}