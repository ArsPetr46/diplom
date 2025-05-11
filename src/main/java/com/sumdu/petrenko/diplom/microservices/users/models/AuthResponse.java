package com.sumdu.petrenko.diplom.microservices.users.models;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для відповіді після успішної автентифікації користувача.
 * <p>
 * Цей клас використовується для передачі токена автентифікації та додаткової
 * інформації про користувача від сервера до клієнта.
 * </p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    /**
     * JWT токен для автентифікованого користувача.
     * Використовується для подальшої авторизації запитів.
     */
    private String token;

    /**
     * Ідентифікатор автентифікованого користувача.
     */
    @Min(value = 1, message = "ID користувача не може бути менше 1")
    private Long userId;
}
