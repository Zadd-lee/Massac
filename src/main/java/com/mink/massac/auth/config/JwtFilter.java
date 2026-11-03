package com.mink.massac.auth.config;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

// Constructed by SecurityConfig, deliberately not registered as a servlet filter bean.
public class JwtFilter extends OncePerRequestFilter {
    private final JwtProvider jwt;
    private final TokenStore store;
    public JwtFilter(JwtProvider jwt, TokenStore store) { this.jwt = jwt; this.store = store; }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return "OPTIONS".equals(request.getMethod()) ||
                List.of("/api/auth/signup", "/api/auth/login", "/api/auth/refresh")
                        .contains(request.getServletPath());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null) {
            try {
                if (!header.startsWith("Bearer ")) throw new IllegalArgumentException();
                var claims = jwt.parse(header.substring(7));
                String username = claims.getSubject();
                String sid = claims.get("sid", String.class);
                if (username == null || username.isBlank() || sid == null || sid.isBlank()
                        || !store.active(username, sid)) {
                    throw new IllegalArgumentException();
                }
                var authentication = new UsernamePasswordAuthenticationToken(username, null,
                        List.of(new SimpleGrantedAuthority("ROLE_USER")));
                authentication.setDetails(sid);
                var context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context);
            } catch (JwtException | IllegalArgumentException e) {
                SecurityContextHolder.clearContext();
                error(response, 401, "Invalid or expired token");
                return;
            } catch (DataAccessException e) {
                SecurityContextHolder.clearContext();
                error(response, 503, "Authentication service unavailable");
                return;
            }
        }
        chain.doFilter(request, response);
    }

    public static void error(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"message\":\"" + message + "\"}");
    }
}
