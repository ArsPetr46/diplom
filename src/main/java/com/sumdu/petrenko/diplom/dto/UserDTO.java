package com.sumdu.petrenko.diplom.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

/**
 * DTO для передачі даних користувача через API.
 * <p>
 * Цей клас представляє публічну інформацію про користувача, яка може бути передана
 * клієнтам через REST API. Він не містить чутливих даних як пароль чи email.
 * </p>
 * <p>
 * Використовується у відповідях API для надання інформації про користувача,
 * включаючи базові дані профілю та час його створення.
 * </p>
 *
 * @see com.sumdu.petrenko.diplom.microservices.users.models.UserEntity Сутність користувача
 * @see com.sumdu.petrenko.diplom.mappers.UserMapper Маппер для конвертації між сутністю та DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Інформація про користувача")
public class UserDTO {
    /**
     * Унікальний ідентифікатор користувача.
     * Відповідає первинному ключу в таблиці користувачів бази даних.
     */
    @Schema(description = "ID користувача")
    private Long id;

    /**
     * Унікальний нікнейм користувача.
     * Використовується для ідентифікації користувача в системі.
     */
    @Schema(description = "Нікнейм користувача")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Нікнейм повинен містити лише латинські літери та цифри")
    @Size(min = 5, max = 30, message = "Нікнейм повинен бути довше 5 символів та не перевищувати 30 символів")
    private String nickname;

    /**
     * Текстовий опис профілю користувача.
     * Може бути null, якщо користувач не додав опис.
     */
    @Schema(description = "Текстовий опис користувача")
    @Size(max = 300, message = "Опис користувача не повинен перевищувати 300 символів")
    private String userDescription;

    /**
     * Дата народження користувача.
     * Може бути null, якщо користувач не вказав дату народження.
     */
    @Schema(description = "Дата народження користувача")
    private LocalDate birthDate;

    /**
     * URL до зображення аватару користувача.
     * Може бути null, якщо користувач не завантажив аватар.
     */
    @Schema(description = "Посилання на аватар користувача")
    private String avatarUrl;

    /**
     * Дата створення облікового запису користувача.
     * Встановлюється автоматично при реєстрації і не може бути змінена.
     */
    @Schema(description = "Час створення користувача")
    private LocalDate userCreationDate;
}
