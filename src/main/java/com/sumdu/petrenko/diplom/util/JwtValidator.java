package com.sumdu.petrenko.diplom.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * Утиліта для валідації JWT (JSON Web Token).
 * <p>
 * Цей клас надає методи для перевірки дійсності токенів, які можуть бути використані для аутентифікації та авторизації користувачів.
 * </p>
 */
public class JwtValidator {
    /**
     * Секретний ключ для підписування токенів.
     * <p>
     * Цей ключ повинен бути збережений в безпечному місці і не повинен бути доступний стороннім особам.
     * </p>
     */
    private JwtValidator() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Валідація JWT токена.
     * <p>
     * Цей метод перевіряє дійсність токена на основі секретного ключа.
     * </p>
     *
     * @param token токен для валідації
     * @return ім'я користувача, якщо токен дійсний
     */
    public static String validateToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(JwtUtil.SECRET.getBytes(StandardCharsets.UTF_8));

        Jws<Claims> claimsJws = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);

        return claimsJws.getPayload().getSubject();
    }
}

