package n11bootcamp_project_backend.user_service.util;

import org.springframework.beans.factory.annotation.Value;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import n11bootcamp_project_backend.user_service.enums.Role;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    // Secret key, application.properties'den gelecek
    @Value("${jwt.secret}")
    private String secretKey;

    // Access token süresi — 1 saat (milisaniye)
    @Value("${jwt.access-token.expiration}")
    private long accessTokenExpiration;

    // Refresh token süresi — 7 gün (milisaniye)
    @Value("${jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    // Secret key'i imzalamak için Key nesnesine çevir
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    // Access token üret
    public String generateAccessToken(UUID userId, Role role) {
        return Jwts.builder()
                .subject(userId.toString())
                .claim("role", role.name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    // Refresh token üret
    public String generateRefreshToken(UUID userId) {
        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    // Token'dan userId çıkar
    public UUID extractUserId(String token) {
        return UUID.fromString(getClaims(token).getSubject());
    }

    // Token'dan role çıkar
    public Role extractRole(String token) {
        return Role.valueOf(getClaims(token).get("role", String.class));
    }

    // Token geçerli mi?
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // Token'dan claims'leri çıkar
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}