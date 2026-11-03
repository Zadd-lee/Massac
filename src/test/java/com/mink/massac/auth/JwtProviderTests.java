package com.mink.massac.auth;

import com.mink.massac.auth.config.JwtProvider;
import io.jsonwebtoken.JwtException;
import java.time.Duration;
import java.util.Base64;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JwtProviderTests {
    private JwtProvider provider(byte value) {
        byte[] key = new byte[32];
        java.util.Arrays.fill(key, value);
        return new JwtProvider(Base64.getEncoder().encodeToString(key), "massac", Duration.ofMinutes(15));
    }

    @Test
    void issuedTokenContainsVerifiedIdentityAndSession() {
        var jwt = provider((byte) 1);
        var claims = jwt.parse(jwt.issue("mink", "session-id"));
        assertEquals("mink", claims.getSubject());
        assertEquals("session-id", claims.get("sid", String.class));
        assertEquals("access", claims.get("type", String.class));
        assertEquals(900, jwt.expiresIn());
    }

    @Test
    void tokenSignedWithAnotherKeyIsRejected() {
        String token = provider((byte) 1).issue("mink", "session-id");
        assertThrows(JwtException.class, () -> provider((byte) 2).parse(token));
    }

    @Test
    void expiredTokenIsRejected() {
        byte[] key = new byte[32];
        java.util.Arrays.fill(key, (byte) 1);
        String token = io.jsonwebtoken.Jwts.builder().issuer("massac").subject("mink")
                .claim("type", "access").expiration(new java.util.Date(0))
                .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(key)).compact();
        assertThrows(io.jsonwebtoken.ExpiredJwtException.class, () -> provider((byte) 1).parse(token));
    }
}
