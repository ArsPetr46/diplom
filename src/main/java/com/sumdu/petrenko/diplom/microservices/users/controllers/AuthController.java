package com.sumdu.petrenko.diplom.microservices.users.controllers;

import com.sumdu.petrenko.diplom.dto.UserDTO;
import com.sumdu.petrenko.diplom.microservices.users.models.AuthRequest;
import com.sumdu.petrenko.diplom.microservices.users.models.AuthResponse;
import com.sumdu.petrenko.diplom.microservices.users.models.UserEntity;
import com.sumdu.petrenko.diplom.microservices.users.services.AuthService;
import com.sumdu.petrenko.diplom.microservices.users.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * Контролер для обробки запитів автентифікації користувачів.
 * <p>
 * Цей контролер надає API для автентифікації користувачів та отримання JWT токенів.
 * </p>
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/auth")
@Tag(name = "Автентифікація", description = "Операції для автентифікації користувачів")
public class AuthController {
    /**
     * Сервіс автентифікації для перевірки облікових даних та генерації токенів.
     */
    private final AuthService authService;

    /**
     * Сервіс користувачів для роботи з даними користувачів.
     */
    private final UserService userService;

    /**
     * Автентифікувати користувача та надати JWT токен.
     * <p>
     * Цей метод обробляє POST-запити для автентифікації користувача за його email та паролем.
     * У разі успішної автентифікації повертає JWT токен та базову інформацію про користувача.
     * </p>
     *
     * @param authRequest Запит автентифікації з email та паролем
     * @return ResponseEntity з токеном та даними користувача або з повідомленням про помилку
     */
    @PostMapping("/login")
    @Operation(
            summary = "Автентифікація користувача",
            description = "Автентифікує користувача за email та паролем і повертає JWT токен для подальшої авторизації.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успішна автентифікація",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = AuthResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Невірні облікові дані",
                            content = @Content),
                    @ApiResponse(responseCode = "400", description = "Некоректний запит",
                            content = @Content)
            }
    )
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest) {
        log.info("Отримано запит на автентифікацію: {}", authRequest);

        try {
            return ResponseEntity.ok(authService.authenticate(authRequest));
        } catch (BadCredentialsException e) {
            log.warn("Невдала спроба автентифікації: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            log.error("Помилка під час автентифікації", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Створити нового користувача.
     * <p>
     * Цей метод обробляє POST-запити для створення нового користувача. Виконує комплексну
     * валідацію даних користувача перед збереженням, включаючи перевірку унікальності email
     * та нікнейму, відсутність ID у запиті, коректність URL аватара та обмеження на дату
     * народження (користувач має бути старшим за 13 років і молодшим за 100 років).
     * </p>
     * <p>
     * Анотація @Valid забезпечує автоматичну валідацію полів об'єкта UserEntity на основі
     * анотацій обмежень, таких як @NotNull, @Size, @Pattern, тощо.
     * </p>
     *
     * @param userEntity Об'єкт користувача, що містить деталі нового користувача. Поле ID повинно
     *                   бути відсутнім (null), оскільки воно генерується автоматично.
     * @return ResponseEntity з об'єктом UserDTO, який представляє створеного користувача, або з
     *         відповідним HTTP-статусом в іншому випадку:
     *         <ul>
     *             <li>201 Created - з об'єктом створеного користувача</li>
     *             <li>400 Bad Request - якщо дані користувача некоректні (невалідний email,
     *                 некоректний пароль, неправильний URL аватара, недопустима дата народження, тощо)</li>
     *             <li>409 Conflict - якщо користувач з таким email або нікнеймом вже існує</li>
     *         </ul>
     */
    @PostMapping("/register")
    @Operation(
            summary = "Створити нового користувача",
            description = "Створює нового користувача на основі наданих даних. Email та нікнейм повинні бути унікальними. Поле ID у запиті має бути відсутнім.",
            tags = {"Створення"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Дані нового користувача",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserEntity.class),
                            examples = {
                                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                                            name = "Базовий приклад",
                                            value = "{\"nickname\": \"user123\", \"email\": \"user@example.com\", \"password\": \"password1234\"}"
                                    )
                            }
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Користувача успішно створено",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Неправильний формат даних (некоректний email, пароль, URL аватара тощо)", content = @Content),
                    @ApiResponse(responseCode = "409", description = "Конфлікт унікальності (користувач з таким email або нікнеймом вже існує)", content = @Content)
            }
    )
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody UserEntity userEntity) {
        log.info("Отримано запит на створення нового користувача: {}", userEntity);

        LocalDate minDate = LocalDate.now().minusYears(100);
        LocalDate maxDate = LocalDate.now().minusYears(13);

        if (userEntity.getBirthDate().isBefore(minDate) || userEntity.getBirthDate().isAfter(maxDate)) {
            log.warn("Вказана некоректна дата народження: {}", userEntity.getBirthDate());
            return ResponseEntity.badRequest().build();
        }

        if (userService.existsByNickname(userEntity.getNickname())) {
            log.warn("Користувач з таким нікнеймом вже існує: {}", userEntity.getNickname());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        if (userService.existsByEmail(userEntity.getEmail())) {
            log.warn("Користувач з таким email вже існує: {}", userEntity.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(userEntity));
        } catch (IllegalArgumentException e) {
            log.warn("Помилка при створенні користувача: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (DataIntegrityViolationException e) {
            log.warn("Порушення унікальності даних при створенні користувача: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}
