package n11bootcamp_project_backend.user_service.services.impl;

import lombok.RequiredArgsConstructor;
import n11bootcamp_project_backend.producer.LogProducer;
import n11bootcamp_project_backend.user_service.util.JwtUtil;
import n11bootcamp_project_backend.user_service.dto.request.LoginRequest;
import n11bootcamp_project_backend.user_service.dto.request.RegisterRequest;
import n11bootcamp_project_backend.user_service.dto.response.TokenResponse;
import n11bootcamp_project_backend.user_service.entity.User;
import n11bootcamp_project_backend.user_service.enums.Role;
import n11bootcamp_project_backend.user_service.repository.UserRepository;
import n11bootcamp_project_backend.user_service.services.AuthService;
import n11bootcamp_project_backend.user_service.services.RedisService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final RedisService redisService;
    private final RabbitTemplate rabbitTemplate;
    private final LogProducer logProducer;

    @Override
    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            logProducer.sendLog("user-service", "ERROR", "Registration failed: Email already exists -> " + request.email());
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .firstName(request.firstName())
                .lastName(request.lastName())
                .role(Role.CUSTOMER)
                .build();

        userRepository.save(user);
        logProducer.sendLog("user-service", "INFO", "User registered successfully with email: " + user.getEmail());

        rabbitTemplate.convertAndSend("saga.exchange", "user.registered", user.getEmail());
    }

    @Override
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseGet(() -> {
                    logProducer.sendLog("user-service", "ERROR", "Login failed: User not found -> " + request.email());
                    throw new RuntimeException("User not found");
                });

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            logProducer.sendLog("user-service", "ERROR", "Login failed: Invalid password for email -> " + request.email());
            throw new RuntimeException("Invalid password");
        }

        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        redisService.saveToken("auth:token:" + user.getId(), accessToken, 3600);
        logProducer.sendLog("user-service", "INFO", "User logged in successfully: " + user.getEmail());

        return new TokenResponse(accessToken, refreshToken, 3600);
    }

    @Override
    public void logout(String token) {
        UUID userId = jwtUtil.extractUserId(token);
        redisService.deleteToken("auth:token:" + userId);
        logProducer.sendLog("user-service", "INFO", "User logged out. UserID: " + userId);
    }

    @Override
    public TokenResponse refreshToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            logProducer.sendLog("user-service", "ERROR", "Refresh token failed: Invalid token");
            throw new RuntimeException("Invalid refresh token");
        }

        UUID userId = jwtUtil.extractUserId(refreshToken);
        User user = userRepository.findById(userId)
                .orElseGet(() -> {
                    logProducer.sendLog("user-service", "ERROR", "Refresh token failed: User not found. ID: " + userId);
                    throw new RuntimeException("User not found");
                });

        String newAccessToken = jwtUtil.generateAccessToken(user.getId(), user.getRole());
        redisService.saveToken("auth:token:" + userId, newAccessToken, 3600);
        logProducer.sendLog("user-service", "INFO", "Token refreshed for UserID: " + userId);

        return new TokenResponse(newAccessToken, refreshToken, 3600);
    }
}