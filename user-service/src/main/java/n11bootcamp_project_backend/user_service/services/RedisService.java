package n11bootcamp_project_backend.user_service.services;

public interface RedisService {

    // Token'ı Redis'e kaydet, TTL saniye cinsinden
    void saveToken(String key, String token, long ttlSeconds);

    // Token'ı getir, yoksa null döner
    String getToken(String key);

    // Token'ı sil (logout)
    void deleteToken(String key);

    // Token hâlâ geçerli mi?
    boolean isTokenValid(String key, String token);
}