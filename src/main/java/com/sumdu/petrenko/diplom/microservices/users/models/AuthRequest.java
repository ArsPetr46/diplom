package com.sumdu.petrenko.diplom.microservices.users.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для запиту автентифікації користувача.
 * <p>
 * Цей клас використовується для передачі даних автентифікації (email та пароль)
 * від клієнта до сервера.
 * </p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {
    /**
     * Email користувача для автентифікації.
     * Має бути валідною адресою електронної пошти.
     */
    @NotBlank(message = "Email не може бути порожнім")
    @Email(message = "Email повинен бути валідним")
    @Size(max = 255, message = "Email не повинен перевищувати 255 символів")
    private String email;

    /**
     * Пароль користувача для автентифікації.
     * Повинен мати довжину від 8 до 30 символів.
     */
    @NotBlank(message = "Пароль не може бути порожнім")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]+$", message = "Пароль повинен складатися з латинських літер і цифр")
    @Size(min = 8, max = 30, message = "Пароль повинен бути від 8 до 30 символів")
    private String password;

}
