package com.mink.massac.auth.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {
    private final SecretKey key;
    private final String issuer;
    private final Duration accessTtl;

    public JwtProvider(@Value("${security.jwt.secret}") String secret,
                       @Value("${security.jwt.issuer}") String issuer,
                       @Value("${security.jwt.access-ttl}") Duration accessTtl) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.issuer = issuer;
        this.accessTtl = accessTtl;
        if (accessTtl.isNegative() || accessTtl.isZero()) {
            throw new IllegalArgumentException("Access TTL must be positive");
        }
    }

    public String issue(String username, String sessionId) {
        Instant now = Instant.now();
        return Jwts.builder().issuer(issuer).subject(username)
                .id(UUID.randomUUID().toString()).claim("type", "access").claim("sid", sessionId)
                .issuedAt(Date.from(now)).expiration(Date.from(now.plus(accessTtl)))
                .signWith(key, Jwts.SIG.HS256).compact();
    }

    public Claims parse(String token) {
        return Jwts.parser().verifyWith(key).requireIssuer(issuer).require("type", "access")
                .build().parseSignedClaims(token).getPayload();
    }

    public long expiresIn() { return accessTtl.toSeconds(); }
}
