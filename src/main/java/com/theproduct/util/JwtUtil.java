package com.theproduct.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecureDigestAlgorithm;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JwtUtil {

    // NOTE: In production, move secret to configuration and rotate keys.
    private static final String SECRET = "change_this_secret_to_a_secure_long_random_value_for_prod";
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    // Auth token validity in milliseconds (e.g., 15 minutes)
    private static final long AUTH_TOKEN_VALIDITY = 15 * 60 * 1000L;
    // Refresh token validity (e.g., 7 days)
    private static final long REFRESH_TOKEN_VALIDITY = 7 * 24 * 60 * 60 * 1000L;

    public static Map<String, Object> generateTokens(String subject) {
        long now = System.currentTimeMillis();
        Date authExpiry = new Date(now + AUTH_TOKEN_VALIDITY);
        Date refreshExpiry = new Date(now + REFRESH_TOKEN_VALIDITY);

        String authToken = Jwts.builder()
                .subject(subject)
                .id(UUID.randomUUID().toString())
                .issuedAt(new Date(now))
                .expiration(authExpiry)
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = Jwts.builder()
                .subject(subject)
                .id(UUID.randomUUID().toString())
                .issuedAt(new Date(now))
                .expiration(refreshExpiry)
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();

        Map<String, Object> map = new HashMap<>();
        map.put("authToken", authToken);
        map.put("authTokenExpiry", authExpiry.getTime());
        map.put("refreshToken", refreshToken);
        map.put("refreshTokenExpiry", refreshExpiry.getTime());
        return map;
    }
}

