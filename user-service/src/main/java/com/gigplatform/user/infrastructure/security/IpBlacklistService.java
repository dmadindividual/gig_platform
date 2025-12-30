package com.gigplatform.user.infrastructure.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class IpBlacklistService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String BLACKLIST_PREFIX = "ip_blacklist:";
    private static final String WHITELIST_PREFIX = "ip_whitelist:";

    /**
     * Block an IP address
     */
    public void blockIp(String ipAddress, Duration duration) {
        String key = BLACKLIST_PREFIX + ipAddress;
        redisTemplate.opsForValue().set(key, "blocked", duration);
        log.warn("IP address blocked: {} for {} hours", ipAddress, duration.toHours());
    }

    /**
     * Block an IP permanently
     */
    public void blockIpPermanently(String ipAddress) {
        String key = BLACKLIST_PREFIX + ipAddress;
        redisTemplate.opsForValue().set(key, "blocked");
        log.warn("IP address permanently blocked: {}", ipAddress);
    }

    /**
     * Check if IP is blocked
     */
    public boolean isBlocked(String ipAddress) {
        String key = BLACKLIST_PREFIX + ipAddress;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * Unblock an IP address
     */
    public void unblockIp(String ipAddress) {
        String key = BLACKLIST_PREFIX + ipAddress;
        redisTemplate.delete(key);
        log.info("IP address unblocked: {}", ipAddress);
    }

    /**
     * Add IP to whitelist (bypass all security checks)
     */
    public void whitelistIp(String ipAddress) {
        String key = WHITELIST_PREFIX + ipAddress;
        redisTemplate.opsForValue().set(key, "whitelisted");
        log.info("IP address whitelisted: {}", ipAddress);
    }

    /**
     * Check if IP is whitelisted
     */
    public boolean isWhitelisted(String ipAddress) {
        String key = WHITELIST_PREFIX + ipAddress;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * Remove IP from whitelist
     */
    public void removeFromWhitelist(String ipAddress) {
        String key = WHITELIST_PREFIX + ipAddress;
        redisTemplate.delete(key);
        log.info("IP address removed from whitelist: {}", ipAddress);
    }

    /**
     * Get time remaining for blocked IP (in seconds)
     */
    public Long getBlockTimeRemaining(String ipAddress) {
        String key = BLACKLIST_PREFIX + ipAddress;
        return redisTemplate.getExpire(key);
    }
}