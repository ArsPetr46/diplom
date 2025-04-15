package com.sumdu.petrenko.diplom.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

public class JwtValidator {
    private JwtValidator() {
        throw new IllegalStateException("Utility class");
    }

    public static String validateToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(JwtUtil.SECRET.getBytes(StandardCharsets.UTF_8));

        Jws<Claims> claimsJws = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);

        return claimsJws.getPayload().getSubject();
    }
}

