package com.mink.massac.auth.config;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HexFormat;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

@Component
public class TokenStore {
    // Compare and rotate in one Redis operation: two refresh requests cannot both succeed.
    private static final DefaultRedisScript<Long> ROTATE = new DefaultRedisScript<>("""
            if redis.call('GET', KEYS[1]) == ARGV[1] then
                redis.call('SET', KEYS[1], ARGV[2], 'KEEPTTL')
                return 1
            end
            return 0
            """, Long.class);
    private final StringRedisTemplate redis;
    private final Duration refreshTtl;

    public TokenStore(StringRedisTemplate redis,
                      @Value("${security.jwt.refresh-ttl}") Duration refreshTtl) {
        this.redis = redis;
        this.refreshTtl = refreshTtl;
        if (refreshTtl.isZero() || refreshTtl.isNegative()) {
            throw new IllegalArgumentException("Refresh TTL must be positive");
        }
    }

    private String key(String username, String sid) {
        return "massac:auth:session:" + hash(username) + ":" + sid;
    }
    public void save(String username, String sid, String refresh) {
        redis.opsForValue().set(key(username, sid), hash(refresh), refreshTtl);
    }
    public boolean active(String username, String sid) {
        return Boolean.TRUE.equals(redis.hasKey(key(username, sid)));
    }
    public boolean rotate(String username, String sid, String oldToken, String newToken) {
        return Long.valueOf(1).equals(redis.execute(ROTATE,
                List.of(key(username, sid)), hash(oldToken), hash(newToken)));
    }
    public void revoke(String username, String sid) { redis.delete(key(username, sid)); }

    private static String hash(String value) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) { throw new IllegalStateException(e); }
    }
}
