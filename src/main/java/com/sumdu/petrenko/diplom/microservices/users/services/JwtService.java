package com.sumdu.petrenko.diplom.microservices.users.services;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Сервіс для роботи з JWT (JSON Web Token).
 * <p>
 * Цей сервіс відповідає за генерацію, валідацію та обробку JWT токенів,
 * які використовуються для автентифікації користувачів.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class JwtService {
    /**
     * Секретний ключ для підпису JWT токенів.
     * Завантажується з конфігурації програми.
     */
    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * Термін дії JWT токена (у мілісекундах).
     * Завантажується з конфігурації програми.
     */
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Згенерувати JWT токен для користувача з додатковими claims.
     *
     * @param userDetails Деталі користувача
     * @return JWT токен
     */
    public String generateToken(UserDetails userDetails) {
        Date createdDate = new Date();

        return Jwts
                .builder()
                .subject(userDetails.getUsername())
                .issuedAt(createdDate)
                .expiration(new Date(createdDate.getTime() + jwtExpiration))
                .signWith(secretKey)
                .compact();
    }

    /**
     * Отримати ім'я користувача (email) з JWT токена.
     *
     * @param token JWT токен
     * @return Email користувача
     */
    public String extractEmail(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Отримати дату закінчення дії JWT токена.
     *
     * @param token JWT токен
     * @return Дата закінчення дії токена
     */
    public Date extractExpiration(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
    }

    /**
     * Перевірити, чи JWT токен валідний для вказаного користувача.
     *
     * @param token JWT токен
     * @return true, якщо токен валідний, false - в іншому випадку
     */
    public boolean isTokenValid(String token) {
        try {
            Date expiration = extractExpiration(token);

            return !expiration.before(new Date());
        } catch (Exception e) {
            throw new AuthenticationCredentialsNotFoundException("JWT токен недійсний", e);
        }
    }
}
