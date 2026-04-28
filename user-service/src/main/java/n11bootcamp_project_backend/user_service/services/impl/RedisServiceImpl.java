package n11bootcamp_project_backend.user_service.services.impl;

import lombok.RequiredArgsConstructor;
import n11bootcamp_project_backend.user_service.services.RedisService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {

    //Lombok @RequiredArgsConstructor sayesinde constructor injection otomatik gerçekleşti.
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void saveToken(String key, String token, long ttlSeconds) {
        stringRedisTemplate.opsForValue().set(key, token, ttlSeconds, TimeUnit.SECONDS);
    }

    @Override
    public String getToken(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    @Override
    public void deleteToken(String key) {
        stringRedisTemplate.delete(key);
    }

    @Override
    public boolean isTokenValid(String key, String token) {
        String savedToken = getToken(key);
        // Token Redis'te var mı ve eşleşiyor mu?
        return savedToken != null && savedToken.equals(token);
    }
}
