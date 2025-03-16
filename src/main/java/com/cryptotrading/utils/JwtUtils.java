package com.cryptotrading.utils;

import com.cryptotrading.db.service.UserService;
import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Component
public class JwtUtils {
    private static final String SECRET_KEY = Dotenv.load().get("SECRET_KEY");
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 10; // 10 hours
    private final UserService userService;
    private static final Set<String> blacklistedTokens = new HashSet<>();
    @Autowired
    public JwtUtils(UserService userService) {
        this.userService = userService;
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String username) {
        return Jwts.builder()
            .subject(username)
            .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .signWith(getSigningKey(), Jwts.SIG.HS256)
            .compact();
    }

    public boolean validateToken(String token) {
        try {
            if (blacklistedTokens.contains(token)) {
                return false;
            }
            Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
    }

    public boolean isValidHeaderWithToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return false;
        }

        token = token.substring(7);

        if (!validateToken(token)) {
            return false;
        }

        String username = extractUsername(token);
        return userService.getUserByUsername(username).isPresent();
    }

    public String extractUsernameFromHeader(String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        return extractUsername(token);
    }

    public void invalidateToken(String token) {
        blacklistedTokens.add(token);
    }
}