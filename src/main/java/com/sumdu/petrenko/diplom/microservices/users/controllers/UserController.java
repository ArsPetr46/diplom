package com.sumdu.petrenko.diplom.microservices.users.controllers;

import com.sumdu.petrenko.diplom.dto.UserDTO;
import com.sumdu.petrenko.diplom.microservices.users.models.UserEntity;
import com.sumdu.petrenko.diplom.microservices.users.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.*;

/**
 * Контролер для обробки запитів, пов'язаних з користувачами.
 * <p>
 * Цей контролер є частиною мікросервісу користувачів і надає RESTful API для створення,
 * отримання, оновлення та видалення користувачів. Він обробляє HTTP-запити, що стосуються
 * сутностей користувачів, та передає їх до відповідного сервісу для виконання бізнес-логіки.
 * </p>
 * <p>
 * Усі методи контролера повертають об'єкти ResponseEntity з відповідними статус-кодами HTTP
 * та повідомленнями про помилки у разі потреби.
 * </p>
 * <p>
 * Використані анотації:
 * <ul>
 *   <li>@RestController - вказує, що клас є контролером REST API, всі методи якого автоматично серіалізують
 *       повернені об'єкти у формат відповіді (за замовчуванням JSON)</li>
 *   <li>@RequiredArgsConstructor - генерує конструктор для всіх полів, помічених як final або @NonNull</li>
 *   <li>@Slf4j - додає до класу автоматично налаштований логер SLF4J</li>
 *   <li>@RequestMapping("/users") - визначає базовий URL для всіх методів контролера</li>
 *   <li>@Tag - додає тег для документації Swagger, що групує API-методи</li>
 * </ul>
 * </p>
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/users")
@Tag(name = "Користувачі", description = "Операції з користувачами")
public class UserController {
    /**
     * Сервіс для роботи з користувачами.
     * <p>
     * Цей сервіс надає методи для виконання операцій з користувачами, такі як пошук, створення,
     * оновлення та видалення. Він інжектується через конструктор завдяки анотації @RequiredArgsConstructor.
     * </p>
     * <p>
     * Взаємодіє з репозиторієм даних для виконання операцій з базою даних та реалізує бізнес-логіку,
     * пов'язану з користувачами.
     * </p>
     */
    private final UserService userService;

    /**
     * Отримати користувача за його id.
     * <p>
     * Цей метод обробляє GET-запити для отримання даних користувача за унікальним ідентифікатором.
     * Виконує валідацію id на стороні контролера перед викликом сервісу.
     * </p>
     *
     * @param id Ідентифікатор користувача, для якого потрібно отримати дані
     * @return ResponseEntity з об'єктом UserDTO, якщо користувача знайдено, або з відповідним HTTP-статусом в іншому випадку:
     *         <ul>
     *             <li>200 OK - якщо користувача знайдено</li>
     *             <li>400 Bad Request - якщо id некоректний (null, від'ємний або нуль)</li>
     *             <li>404 Not Found - якщо користувача з вказаним id не знайдено</li>
     *         </ul>
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Отримати користувача за id",
            description = "Знаходить інформацію про користувача за його id. Повертає детальну інформацію про користувача.",
            tags = {"Отримання"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Користувача знайдено",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Неправильний формат id (від'ємне або нуль)", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Користувача з вказаним id не знайдено", content = @Content)
            }
    )
    public ResponseEntity<UserDTO> getUserById(
            @Parameter(description = "Id користувача", example = "42")
            @PathVariable Long id) {
        log.info("Отримано запит на пошук користувача з id {}", id);

        if (id == null || id <= 0) {
            log.warn("Отримано запит з неправильним id: {}", id);
            return ResponseEntity.badRequest().build();
        }

        Optional<UserDTO> userOptional = userService.getUserById(id);

        return userOptional.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Перевірити, чи існує користувач за його id.
     * <p>
     * Цей метод використовується для швидкої перевірки існування користувача без необхідності
     * отримувати всі його дані. Може використовуватися іншими мікросервісами для валідації
     * посилань на користувачів.
     * </p>
     *
     * @param id Ідентифікатор користувача, якого потрібно перевірити
     * @return ResponseEntity без тіла, але з відповідним HTTP-статусом:
     *         <ul>
     *             <li>200 OK - якщо користувач існує</li>
     *             <li>400 Bad Request - якщо id некоректний (null, від'ємний або нуль)</li>
     *             <li>404 Not Found - якщо користувача з вказаним id не знайдено</li>
     *         </ul>
     */
    @GetMapping("/{id}/exists")
    @Operation(
            summary = "Перевірити існування користувача",
            description = "Перевіряє чи існує користувач із зазначеним id. Повертає 200 OK, якщо користувач існує, або 404 Not Found, якщо користувача немає.",
            tags = {"Перевірка"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Користувач існує", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Неправильний формат id (від'ємне або нуль)", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Користувача з вказаним id не знайдено", content = @Content)
            }
    )
    public ResponseEntity<Void> existsById(
            @Parameter(description = "Id користувача", example = "42")
            @PathVariable Long id) {
        log.info("Отримано запит на перевірку існування користувача з id {}", id);

        if (id == null || id <= 0) {
            log.warn("Отримано запит з неправильним id: {}", id);
            return ResponseEntity.badRequest().build();
        }

        return userService.existsById(id)
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }

    /**
     * Отримати користувачів за частиною їх нікнейму.
     * <p>
     * Цей метод реалізує функціонал пошуку користувачів за нікнеймом. Пошук не чутливий до
     * регістру та повертає всіх користувачів, чий нікнейм містить вказаний підрядок.
     * Метод виконує валідацію пошукового запиту на відповідність регулярному виразу та
     * мінімальній довжині.
     * </p>
     *
     * @param nickname Частина нікнейму для пошуку користувачів. Має містити лише латинські
     *                літери та цифри, а також мати довжину не менше 3 символів.
     * @return ResponseEntity зі списком об'єктів UserDTO, які відповідають критеріям пошуку, або з
     *         відповідним HTTP-статусом в іншому випадку:
     *         <ul>
     *             <li>200 OK - зі списком знайдених користувачів (список може бути порожнім)</li>
     *             <li>400 Bad Request - якщо нікнейм некоректний (null, порожній, містить
     *                 недопустимі символи або має довжину менше 3 символів)</li>
     *         </ul>
     */
    @GetMapping("/search/nickname/{nickname}")
    @Operation(
            summary = "Пошук користувачів за нікнеймом",
            description = "Знаходить користувачів, нікнейм яких містить вказану строку. Пошук не чутливий до регістру. Нікнейм має містити мінімум 3 символи та лише латинські літери й цифри.",
            tags = {"Пошук"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список знайдених користувачів (може бути порожнім)",
                            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserDTO.class)))),
                    @ApiResponse(responseCode = "400", description = "Неправильний формат нікнейму (менше 3 символів або містить недопустимі символи)", content = @Content)
            }
    )
    public ResponseEntity<List<UserDTO>> getUsersByNicknameContaining(
            @Parameter(description = "Нікнейм користувача", example = "user123")
            @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Пошуковий запит має містити лише латинські літери та цифри")
            @Size(min = 3, message = "Пошуковий запит має містити щонайменше 3 символи")
            @PathVariable String nickname) {
        log.info("Отримано запит на пошук користувачів з нікнеймом {}", nickname);

        if (nickname == null || nickname.isEmpty()) {
            log.warn("Отримано запит з порожнім нікнеймом");
            return ResponseEntity.badRequest().build();
        }

        try {
            List<UserDTO> users = userService.getUsersByNicknameContaining(nickname);
            log.info("Знайдено {} користувачів за запитом '{}'", users.size(), nickname);
            return ResponseEntity.ok(users);
        } catch (IllegalArgumentException e) {
            log.warn("Помилка при пошуку користувачів за нікнеймом: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Отримати користувачів за списком їх ID.
     * <p>
     * Цей метод дозволяє отримати дані кількох користувачів за один запит, що оптимізує
     * взаємодію між клієнтами та сервером. Метод виконує ретельну валідацію вхідних даних
     * перед виконанням запиту до бази даних, включаючи перевірку на null, порожні списки,
     * недійсні ID та занадто великі списки.
     * </p>
     * <p>
     * При наявності дублікатів ID у списку, вони будуть автоматично видалені перед виконанням запиту.
     * </p>
     *
     * @param userIds Список ідентифікаторів користувачів для пошуку. Має бути непорожнім,
     *                містити не більше 1000 елементів, не містити null, від'ємних або нульових значень.
     * @return ResponseEntity зі списком об'єктів UserDTO, які відповідають вказаним ID, або з
     *         відповідним HTTP-статусом в іншому випадку:
     *         <ul>
     *             <li>200 OK - зі списком знайдених користувачів</li>
     *             <li>400 Bad Request - якщо список ID некоректний (null, порожній, містить
     *                 недійсні значення або занадто багато елементів)</li>
     *             <li>404 Not Found - якщо жодного користувача не знайдено за вказаними ID</li>
     *             <li>500 Internal Server Error - у разі непередбаченої помилки під час обробки запиту</li>
     *         </ul>
     */
    @PostMapping("/getByIds")
    @Operation(
            summary = "Отримати користувачів за списком ID",
            description = "Виконує пошук користувачів за списком id. Список повинен містити не більше 1000 значень, бути непорожнім, та не містити null, від'ємних або нульових значень.",
            tags = {"Пакетне отримання"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Список ID користувачів для пошуку",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = Long.class),
                            examples = {
                                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                                            name = "Базовий приклад",
                                            value = "[1, 2, 3, 42]"
                                    )
                            }
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список знайдених користувачів",
                            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserDTO.class)))),
                    @ApiResponse(responseCode = "400", description = "Неправильний формат запиту (порожній список, забагато елементів, недійсні ID)", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Жодного користувача не знайдено за вказаними ID", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Внутрішня помилка сервера", content = @Content)
            }
    )
    public ResponseEntity<List<UserDTO>> getUsersByIds(
            @Parameter(description = "Список id") @RequestBody List<Long> userIds) {
        log.info("Отримано запит на пошук користувачів за списком id: {}", userIds);

        try {
            if (userIds == null || userIds.isEmpty()) {
                log.warn("Отримано запит з порожнім списком ID користувачів");
                return ResponseEntity.badRequest().build();
            }

            if (userIds.size() > 1000) {
                log.warn("Запит містить забагато ID користувачів: {}", userIds.size());
                return ResponseEntity.badRequest().build();
            }

            if (userIds.contains(null)) {
                log.warn("Запит містить null значення в списку ID");
                return ResponseEntity.badRequest().build();
            }

            List<Long> invalidIds = userIds.stream().filter(id -> id < 0).toList();
            if (!invalidIds.isEmpty()) {
                log.warn("Запит містить недійсні (від'ємні або нульові) ID: {}", invalidIds);
                return ResponseEntity.badRequest().build();
            }

            Set<Long> uniqueIds = new HashSet<>(userIds);
            if (uniqueIds.size() < userIds.size()) {
                log.info("Запит містить дублікати ID, вони будуть проігноровані");
                userIds = new ArrayList<>(uniqueIds);
            }

            List<UserDTO> users = userService.getUsersByIds(userIds);

            return users.isEmpty()
                    ? ResponseEntity.notFound().build()
                    : ResponseEntity.ok(users);
        } catch (IllegalArgumentException e) {
            log.warn("Помилка при пошуку користувачів за ID: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Непередбачена помилка при пошуку користувачів за ID: {}", e.getMessage());
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
    @PostMapping(consumes = "application/json", produces = "application/json")
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
    public ResponseEntity<UserDTO> createUser(
            @Valid @RequestBody UserEntity userEntity) {
        log.info("Отримано запит на створення нового користувача: {}", userEntity);

        if (userEntity.getId() != null) {
            log.warn("При створенні користувача вказано ID: {}", userEntity.getId());
            return ResponseEntity.badRequest().body(null);
        }

        if (userEntity.getBirthDate() != null) {
            LocalDate minDate = LocalDate.now().minusYears(100);
            LocalDate maxDate = LocalDate.now().minusYears(13);

            if (userEntity.getBirthDate().isBefore(minDate) || userEntity.getBirthDate().isAfter(maxDate)) {
                log.warn("Вказана некоректна дата народження: {}", userEntity.getBirthDate());
                return ResponseEntity.badRequest().build();
            }
        }

        if (userService.existsByNickname(userEntity.getNickname())) {
            log.warn("Користувач з таким нікнеймом вже існує: {}", userEntity.getNickname());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        if (userService.existsByEmail(userEntity.getEmail())) {
            log.warn("Користувач з таким email вже існує: {}", userEntity.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        if (userEntity.getAvatarUrl() != null && !userEntity.getAvatarUrl().isEmpty()) {
            try {
                URI uri = new URI(userEntity.getAvatarUrl());
                if (!uri.isAbsolute() || uri.getScheme() == null) {
                    log.warn("Вказаний некоректний URL аватара (відсутня схема): {}", userEntity.getAvatarUrl());
                    return ResponseEntity.badRequest().body(null);
                }
            } catch (URISyntaxException _) {
                log.warn("Вказаний некоректний URL аватара: {}", userEntity.getAvatarUrl());
                return ResponseEntity.badRequest().body(null);
            }
        }

        try {
            UserDTO createdUser = userService.saveUser(userEntity);
            log.info("Створено нового користувача з ID: {}", createdUser.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (IllegalArgumentException e) {
            log.warn("Помилка при створенні користувача: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (DataIntegrityViolationException e) {
            log.warn("Порушення унікальності даних при створенні користувача: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }

    /**
     * Оновити існуючого користувача.
     * <p>
     * Цей метод обробляє PUT-запити для оновлення даних існуючого користувача. Виконує
     * валідацію ID та інших даних користувача перед оновленням. ID у шляху URL повинен
     * відповідати ID у тілі запиту, якщо він вказаний.
     * </p>
     * <p>
     * Анотація @Valid забезпечує автоматичну валідацію полів об'єкта UserEntity на основі
     * анотацій обмежень для всіх полів, які будуть оновлені.
     * </p>
     * <p>
     * При оновленні важливо пам'ятати, що деякі поля користувача не можуть бути змінені після
     * створення (наприклад, email).
     * </p>
     *
     * @param id Ідентифікатор користувача, якого потрібно оновити
     * @param userEntityDetails Об'єкт користувача з новими даними. Якщо в ньому вказаний ID,
     *                          він повинен відповідати ID у шляху URL.
     * @return ResponseEntity з оновленим об'єктом UserDTO або з відповідним HTTP-статусом в іншому випадку:
     *         <ul>
     *             <li>200 OK - з оновленим об'єктом користувача</li>
     *             <li>400 Bad Request - якщо дані користувача некоректні або ID у шляху не відповідає ID у тілі запиту</li>
     *             <li>404 Not Found - якщо користувача з вказаним ID не знайдено</li>
     *             <li>409 Conflict - якщо новий нікнейм вже використовується іншим користувачем</li>
     *         </ul>
     */
    @PutMapping("/{id}")
    @Operation(
            summary = "Оновити дані користувача",
            description = "Оновлює інформацію про існуючого користувача. ID у шляху повинен відповідати ID у тілі запиту, якщо він вказаний. Email не може бути змінений.",
            tags = {"Оновлення"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Дані користувача успішно оновлено",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Неправильний формат даних або невідповідність ID", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Користувача з вказаним ID не знайдено", content = @Content),
                    @ApiResponse(responseCode = "409", description = "Конфлікт унікальності (новий нікнейм вже використовується)", content = @Content)
            }
    )
    public ResponseEntity<UserDTO> updateUser(
            @Parameter(description = "ID користувача для оновлення", example = "42") @PathVariable Long id,
            @Valid @RequestBody UserEntity userEntityDetails) {
        log.info("Отримано запит на оновлення користувача з id {}: {}", id, userEntityDetails);

        if (userEntityDetails.getId() != null && !userEntityDetails.getId().equals(id)) {
            log.warn("ID у шляху ({}) не відповідає ID у тілі запиту ({})", id, userEntityDetails.getId());
            return ResponseEntity.badRequest().build();
        }

        try {
            return userService.updateUser(id, userEntityDetails)
                    .map(dto -> {
                        log.info("Дані користувача з id {} оновлено", id);
                        return ResponseEntity.ok(dto);
                    })
                    .orElseGet(() -> {
                        log.warn("Користувача з id {} не знайдено", id);
                        return ResponseEntity.notFound().build();
                    });
        } catch (IllegalArgumentException e) {
            log.warn("Помилка при оновленні користувача: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (DataIntegrityViolationException e) {
            log.warn("Порушення унікальності даних при оновленні користувача: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    /**
     * Видалити користувача за його id.
     * <p>
     * Цей метод обробляє DELETE-запити для видалення користувача з системи. Виконує валідацію
     * ID та перевіряє існування користувача перед видаленням. Операція є незворотною.
     * </p>
     * <p>
     * Якщо з користувачем пов'язані інші дані в системі (повідомлення, чати, тощо), видалення
     * може призвести до помилки порушення цілісності даних. У такому випадку необхідно спочатку
     * видалити всі пов'язані дані або використовувати спеціальний механізм каскадного видалення.
     * </p>
     *
     * @param id Ідентифікатор користувача, якого потрібно видалити
     * @return ResponseEntity без тіла, але з відповідним HTTP-статусом:
     *         <ul>
     *             <li>204 No Content - якщо користувача успішно видалено</li>
     *             <li>400 Bad Request - якщо ID некоректний (null, від'ємний або нуль)</li>
     *             <li>404 Not Found - якщо користувача з вказаним ID не знайдено</li>
     *             <li>409 Conflict - якщо неможливо видалити користувача через пов'язані дані</li>
     *             <li>500 Internal Server Error - у разі непередбаченої помилки під час обробки запиту</li>
     *         </ul>
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Видалити користувача",
            description = "Видаляє користувача з вказаним ID. Операція є незворотною. Якщо з користувачем пов'язані інші дані (повідомлення, чати тощо), видалення може бути неможливим без попереднього видалення цих даних.",
            tags = {"Видалення"},
            responses = {
                    @ApiResponse(responseCode = "204", description = "Користувача успішно видалено", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Неправильний формат ID (від'ємне або нуль)", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Користувача з вказаним ID не знайдено", content = @Content),
                    @ApiResponse(responseCode = "409", description = "Неможливо видалити користувача через зв'язані дані", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Внутрішня помилка сервера", content = @Content)
            }
    )
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "Id користувача", example = "42")
            @PathVariable Long id) {
        log.info("Отримано запит на видалення користувача з id {}", id);

        if (id == null || id <= 0) {
            log.warn("Отримано запит з неправильним id: {}", id);
            return ResponseEntity.badRequest().build();
        }

        try {
            if (userService.deleteUser(id)) {
                log.info("Користувача з id {} успішно видалено", id);
                return ResponseEntity.noContent().build();
            } else {
                log.warn("Не знайдено користувача з id {} для видалення", id);
                return ResponseEntity.notFound().build();
            }
        } catch (DataIntegrityViolationException e) {
            log.warn("Неможливо видалити користувача з id {} через зв'язані дані: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            log.error("Помилка при видаленні користувача з id {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}