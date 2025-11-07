package com.example.boho_v10.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenService {
    private static class Session {
        final String username;
        final Instant expiresAt;
        Session(String username, Instant expiresAt) { this.username = username; this.expiresAt = expiresAt; }
    }

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    private static final long TTL_MIN = 8 * 60; // 8 часов

    public String issueToken(String username) {
        String token = UUID.randomUUID().toString();
        sessions.put(token, new Session(username, Instant.now().plusSeconds(TTL_MIN * 60)));
        return token;
    }

    public boolean isValid(String token) {
        if (token == null || token.isBlank()) return false;
        Session s = sessions.get(token);
        if (s == null) return false;
        if (Instant.now().isAfter(s.expiresAt)) {
            sessions.remove(token);
            return false;
        }
        return true;
    }

    public void revoke(String token) {
        if (token != null) sessions.remove(token);
    }
}