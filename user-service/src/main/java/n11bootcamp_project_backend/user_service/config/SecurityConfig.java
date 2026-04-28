package n11bootcamp_project_backend.user_service.config;

import lombok.RequiredArgsConstructor;
import n11bootcamp_project_backend.user_service.filter.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF'yi kapat — JWT kullandığımız için gerek yok
                .csrf(csrf -> csrf.disable())

                // Session tutma — JWT stateless
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Endpoint yetkilendirme
                .authorizeHttpRequests(auth -> auth
                        // Bu endpoint'ler herkese açık
                        .requestMatchers("/api/auth/register", "/api/auth/login","/swagger-ui/**", "/v3/api-docs/**", "/api/auth/refresh").permitAll()
                        // Geri kalanlar token ister
                        .anyRequest().authenticated())

                // JWT filter'ı Spring Security'nin önüne ekle
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // PasswordEncoder bean'ini tanımlıyoruz,
    // PasswordEncoder için hangi algoritma kullanacağını Spring bilmiyor.BCrypt mi, SHA256 mi? Buna karar verip tanımlıyoruz. Standart olduğu için BCrypt seçtim.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}