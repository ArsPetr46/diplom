package com.sumdu.petrenko.diplom.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

/**
 * Утиліта для роботи з JWT (JSON Web Token).
 * <p>
 * Цей клас надає методи для генерації токенів, які можуть бути використані для аутентифікації та авторизації користувачів.
 * </p>
 */
public class JwtUtil {
    /**
     * Секретний ключ для підписування токенів.
     * <p>
     * Цей ключ повинен бути збережений в безпечному місці і не повинен бути доступний стороннім особам.
     * </p>
     */
    protected static final String SECRET = "diplom12345678910";
    /**
     * Ключ для підписування токенів.
     * <p>
     * Цей ключ генерується з секретного рядка за допомогою Base64 декодування.
     * </p>
     */
    private static final Key SECRET_KEY = Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET));

    /**
     * Конструктор класу JwtUtil.
     * <p>
     * Цей конструктор є приватним, оскільки цей клас є утилітою і не повинен бути створений.
     * </p>
     */
    private JwtUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Генерація JWT токена.
     * <p>
     * Цей метод генерує токен на основі імені користувача та часу його дії.
     * </p>
     *
     * @param username ім'я користувача
     * @return згенерований токен
     */
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

