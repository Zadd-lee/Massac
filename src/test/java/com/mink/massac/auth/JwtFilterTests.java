package com.mink.massac.auth;

import java.time.Duration;
import java.util.Base64;

import com.mink.massac.auth.config.JwtFilter;
import com.mink.massac.auth.config.JwtProvider;
import com.mink.massac.auth.config.TokenStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtFilterTests {
    @AfterEach
    void clearContext() { SecurityContextHolder.clearContext(); }

    @Test
    void revokedSessionRejectsOtherwiseValidJwt() throws Exception {
        var jwt = new JwtProvider(Base64.getEncoder().encodeToString(new byte[32]),
                "massac", Duration.ofMinutes(15));
        var store = mock(TokenStore.class);
        when(store.active("mink", "sid")).thenReturn(false);
        var request = new MockHttpServletRequest("GET", "/api/auth/me");
        request.addHeader("Authorization", "Bearer " + jwt.issue("mink", "sid"));
        var response = new MockHttpServletResponse();
        var chain = mock(jakarta.servlet.FilterChain.class);
        new JwtFilter(jwt, store).doFilter(request, response, chain);
        assertEquals(401, response.getStatus());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verifyNoInteractions(chain);
    }

    @Test
    void redisFailureRejectsRequestWithServiceUnavailable() throws Exception {
        var jwt = new JwtProvider(Base64.getEncoder().encodeToString(new byte[32]),
                "massac", Duration.ofMinutes(15));
        var store = mock(TokenStore.class);
        when(store.active("mink", "sid")).thenThrow(
                new org.springframework.data.redis.RedisConnectionFailureException("offline"));
        var request = new MockHttpServletRequest("GET", "/api/auth/me");
        request.addHeader("Authorization", "Bearer " + jwt.issue("mink", "sid"));
        var response = new MockHttpServletResponse();
        var chain = mock(jakarta.servlet.FilterChain.class);
        new JwtFilter(jwt, store).doFilter(request, response, chain);
        assertEquals(503, response.getStatus());
        verifyNoInteractions(chain);
    }
}
