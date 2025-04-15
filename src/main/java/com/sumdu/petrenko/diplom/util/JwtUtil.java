package com.sumdu.petrenko.diplom.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

public class JwtUtil {
    protected static final String SECRET = "diplom12345678910";
    private static final Key SECRET_KEY = Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET));

    private JwtUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static String generateToken(String username) {
        long expirationTime = 1000 * 60 * 60 * 24L;

        return Jwts.builder()
                .claim("sub", username)
                .claim("iat", new Date().getTime())
                .claim("exp", new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SECRET_KEY)
                .compact();
    }
}

