package com.gigplatform.user.infrastructure.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class RateLimiterService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String RATE_LIMIT_PREFIX = "rate_limit:";

    /**
     * Check if request is allowed under rate limit
     * @param key Unique identifier (e.g., "login:192.168.1.1")
     * @param maxAttempts Maximum allowed attempts
     * @param window Time window
     * @return true if allowed, false if rate limit exceeded
     */
    public boolean isAllowed(String key, int maxAttempts, Duration window) {
        String redisKey = RATE_LIMIT_PREFIX + key;

        try {
            // Increment counter
            Long currentCount = redisTemplate.opsForValue().increment(redisKey);

            if (currentCount == null) {
                log.warn("Failed to increment rate limit counter for key: {}", key);
                return true;  // Fail open
            }

            // Set expiry on first request
            if (currentCount == 1) {
                redisTemplate.expire(redisKey, window);
            }

            boolean allowed = currentCount <= maxAttempts;

            if (!allowed) {
                log.warn("Rate limit exceeded for key: {}. Count: {}, Max: {}",
                        key, currentCount, maxAttempts);
            }

            return allowed;

        } catch (Exception e) {
            log.error("Rate limiter error for key: {}", key, e);
            return true;  // Fail open (allow request if Redis is down)
        }
    }

    /**
     * Reset rate limit counter for a key
     */
    public void reset(String key) {
        String redisKey = RATE_LIMIT_PREFIX + key;
        redisTemplate.delete(redisKey);
        log.debug("Rate limit reset for key: {}", key);
    }

    /**
     * Get current count for a key
     */
    public long getCurrentCount(String key) {
        String redisKey = RATE_LIMIT_PREFIX + key;
        String count = redisTemplate.opsForValue().get(redisKey);
        return count != null ? Long.parseLong(count) : 0;
    }
}