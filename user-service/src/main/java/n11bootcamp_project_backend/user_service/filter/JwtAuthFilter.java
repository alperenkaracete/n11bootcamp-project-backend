package n11bootcamp_project_backend.user_service.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import n11bootcamp_project_backend.user_service.util.JwtUtil;
import n11bootcamp_project_backend.user_service.enums.Role;
import n11bootcamp_project_backend.user_service.services.RedisService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final RedisService redisService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Header'dan token'ı al
        String authHeader = request.getHeader("Authorization");

        // Token yoksa veya Bearer ile başlamıyorsa geç
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // "Bearer " kısmını at
        String token = authHeader.substring(7);

        // Token geçerli mi?
        if (!jwtUtil.validateToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Token'dan userId çıkar
        UUID userId = jwtUtil.extractUserId(token);

        // Redis'te bu token var mı?
        if (!redisService.isTokenValid("auth:token:" + userId, token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Role'ü çıkar
        Role role = jwtUtil.extractRole(token);

        // Spring Security'ye kullanıcıyı tanıt
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userId,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + role.name()))
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}