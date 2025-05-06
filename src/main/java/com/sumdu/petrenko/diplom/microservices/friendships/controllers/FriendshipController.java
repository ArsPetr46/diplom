package com.sumdu.petrenko.diplom.microservices.friendships.controllers;

import com.sumdu.petrenko.diplom.dto.FriendshipDTO;
import com.sumdu.petrenko.diplom.dto.UserDTO;
import com.sumdu.petrenko.diplom.microservices.friendships.models.FriendshipEntity;
import com.sumdu.petrenko.diplom.microservices.friendships.services.FriendshipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

/**
 * Контролер для обробки запитів, пов'язаних з дружбами.
 * <p>
 * Цей контролер є частиною мікросервісу дружб і надає RESTful API для створення,
 * отримання, оновлення та керування дружбами між користувачами системи. Він обробляє HTTP-запити,
 * що стосуються сутностей дружби, та передає їх до відповідного сервісу для виконання бізнес-логіки.
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
 *   <li>@RequestMapping("api/friendships") - визначає базовий URL для всіх методів контролера</li>
 *   <li>@Tag - додає тег для документації Swagger, що групує API-методи</li>
 * </ul>
 * </p>
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("api/friendships")
@Tag(name = "Дружби", description = "Операції з дружбами між користувачами")
public class FriendshipController {
    /**
     * Сервіс для роботи з дружбами.
     * <p>
     * Цей сервіс надає методи для виконання операцій з дружбами, такі як пошук, створення,
     * оновлення та інші операції. Він інжектується через конструктор завдяки анотації @RequiredArgsConstructor.
     * </p>
     * <p>
     * Взаємодіє з репозиторієм даних для виконання операцій з базою даних та реалізує бізнес-логіку,
     * пов'язану з дружбами між користувачами.
     * </p>
     */
    private final FriendshipService friendshipService;

    /**
     * Отримати запис про дружбу за його композитним id.
     * <p>
     * Цей метод обробляє GET-запити для отримання даних про дружбу за ідентифікаторами двох користувачів.
     * Виконує пошук запису про дружбу і повертає його, якщо такий існує.
     * </p>
     *
     * @param userId1 ід першого користувача
     * @param userId2 ід другого користувача
     * @return ResponseEntity з об'єктом FriendshipEntity, якщо дружбу знайдено, або з відповідним HTTP-статусом в іншому випадку:
     *         <ul>
     *             <li>200 OK - якщо дружбу знайдено</li>
     *             <li>404 Not Found - якщо дружбу не знайдено</li>
     *             <li>500 Internal Server Error - у разі непередбаченої помилки під час обробки запиту</li>
     *         </ul>
     */
    @GetMapping()
    @Operation(
            summary = "Отримати дружбу за ID",
            description = "Знаходить інформацію про дружбу за ідентифікаторами двох користувачів",
            tags = {"Отримання"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Дружбу знайдено",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = FriendshipEntity.class))),
                    @ApiResponse(responseCode = "404", description = "Дружбу не знайдено", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Внутрішня помилка сервера", content = @Content)
            }
    )
    public ResponseEntity<FriendshipEntity> getFriendshipById(
            @Parameter(description = "ID першого користувача", example = "1") @RequestParam Long userId1,
            @Parameter(description = "ID другого користувача", example = "2") @RequestParam Long userId2) {
        try {
            Optional<FriendshipEntity> friendship = friendshipService.getFriendshipById(userId1, userId2);

            return friendship.map(ResponseEntity::ok)
                    .orElseGet(() -> {
                        log.warn("Дружбу між користувачами з ID {} та {} не знайдено", userId1, userId2);
                        return ResponseEntity.notFound().build();
                    });
        } catch (DataAccessException e) {
            log.error("Помилка бази даних при отриманні дружби між користувачами {} та {}: {}", userId1, userId2, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Помилка бази даних при отриманні дружби", e);
        } catch (Exception e) {
            log.error("Непередбачена помилка при отриманні дружби між користувачами {} та {}: {}", userId1, userId2, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Непередбачена помилка при отриманні дружби", e);
        }
    }

    /**
     * Отримати список друзів для конкретного користувача.
     * <p>
     * Цей метод обробляє GET-запити для отримання списку всіх друзів вказаного користувача.
     * Метод повертає список об'єктів FriendshipDTO, які містять інформацію про друзів та статус дружби.
     * </p>
     *
     * @param userId ід користувача, для якого потрібно отримати друзів
     * @return ResponseEntity зі списком об'єктів FriendshipDTO, або з відповідним HTTP-статусом в іншому випадку:
     *         <ul>
     *             <li>200 OK - якщо список друзів успішно отримано (може бути порожнім)</li>
     *             <li>404 Not Found - якщо друзів не знайдено</li>
     *             <li>500 Internal Server Error - у разі непередбаченої помилки під час обробки запиту</li>
     *         </ul>
     */
    @GetMapping("/user/{userId}")
    @Operation(
            summary = "Отримати друзів користувача",
            description = "Знаходить список друзів для конкретного користувача за його ідентифікатором",
            tags = {"Отримання"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список друзів успішно отримано",
                            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = FriendshipDTO.class)))),
                    @ApiResponse(responseCode = "404", description = "Друзів не знайдено", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Внутрішня помилка сервера", content = @Content)
            }
    )
    public ResponseEntity<List<FriendshipDTO>> getFriendsOfUser(
            @Parameter(description = "ID користувача", example = "42") @PathVariable Long userId) {
        try {
            if (userId == null || userId <= 0) {
                log.warn("Запит на отримання друзів з некоректним ID користувача: {}", userId);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID користувача має бути додатним числом");
            }

            List<FriendshipDTO> friends = friendshipService.getFriendsOfUser(userId);

            if (friends.isEmpty()) {
                log.warn("Друзів для користувача з ID {} не знайдено", userId);
                return ResponseEntity.notFound().build();
            }

            log.info("Знайдено {} друзів для користувача з ID {}", friends.size(), userId);
            return ResponseEntity.ok(friends);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (DataAccessException e) {
            log.error("Помилка бази даних при отриманні друзів користувача {}: {}", userId, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Помилка бази даних при отриманні друзів", e);
        } catch (Exception e) {
            log.error("Непередбачена помилка при отриманні друзів користувача {}: {}", userId, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Непередбачена помилка при отриманні друзів", e);
        }
    }

    /**
     * Перевірити, чи існує дружба між двома користувачами.
     * <p>
     * Цей метод обробляє GET-запити для перевірки існування дружби між двома користувачами.
     * Метод використовується для швидкої перевірки статусу дружби без необхідності
     * отримувати всі деталі відносин.
     * </p>
     *
     * @param userId1 ід першого користувача
     * @param userId2 ід другого користувача
     * @return ResponseEntity з булевим значенням, або з відповідним HTTP-статусом в іншому випадку:
     *         <ul>
     *             <li>200 OK з тілом true - якщо дружба існує</li>
     *             <li>404 Not Found з тілом false - якщо дружба не існує</li>
     *             <li>400 Bad Request - якщо параметри запиту некоректні</li>
     *             <li>500 Internal Server Error - у разі непередбаченої помилки під час обробки запиту</li>
     *         </ul>
     */
    @GetMapping("/exists")
    @Operation(
            summary = "Перевірити існування дружби",
            description = "Перевіряє, чи існує дружба між двома користувачами за їх ідентифікаторами",
            tags = {"Перевірка"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Дружба існує",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))),
                    @ApiResponse(responseCode = "404", description = "Дружба не існує",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))),
                    @ApiResponse(responseCode = "400", description = "Некоректні параметри запиту", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Внутрішня помилка сервера", content = @Content)
            }
    )
    public ResponseEntity<Boolean> existsByUserIds(
            @Parameter(description = "ID першого користувача", example = "1") @RequestParam Long userId1,
            @Parameter(description = "ID другого користувача", example = "2") @RequestParam Long userId2) {
        try {
            if (userId1 == null || userId1 <= 0 || userId2 == null || userId2 <= 0) {
                log.warn("Запит на перевірку дружби з некоректними ID користувачів: {} та {}", userId1, userId2);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID користувачів мають бути додатними числами");
            }

            boolean exists = friendshipService.existsFriendship(userId1, userId2);

            if (exists) {
                log.info("Дружба між користувачами з ID {} та {} існує", userId1, userId2);
                return ResponseEntity.ok(true);
            } else {
                log.warn("Дружба між користувачами з ID {} та {} не існує", userId1, userId2);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
            }
        } catch (ResponseStatusException e) {
            throw e;
        } catch (DataAccessException e) {
            log.error("Помилка бази даних при перевірці дружби між користувачами {} та {}: {}", userId1, userId2, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Помилка бази даних при перевірці дружби", e);
        } catch (Exception e) {
            log.error("Непередбачена помилка при перевірці дружби між користувачами {} та {}: {}", userId1, userId2, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Непередбачена помилка при перевірці дружби", e);
        }
    }

    /**
     * Отримати кількість спільних друзів для двох користувачів.
     * <p>
     * Цей метод обробляє GET-запити для отримання кількості спільних друзів між двома користувачами.
     * Використовується для відображення інформації про спільне коло знайомств.
     * </p>
     *
     * @param userId1 ід першого користувача
     * @param userId2 ід другого користувача
     * @return ResponseEntity з кількістю спільних друзів, або з відповідним HTTP-статусом в іншому випадку:
     *         <ul>
     *             <li>200 OK - з кількістю спільних друзів (може бути 0)</li>
     *             <li>400 Bad Request - якщо параметри запиту некоректні</li>
     *             <li>500 Internal Server Error - у разі непередбаченої помилки під час обробки запиту</li>
     *         </ul>
     */
    @GetMapping("/mutual_friends")
    @Operation(
            summary = "Отримати кількість спільних друзів",
            description = "Повертає кількість спільних друзів між двома користувачами",
            tags = {"Перевірка"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Кількість спільних друзів успішно отримано",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Integer.class))),
                    @ApiResponse(responseCode = "400", description = "Некоректні параметри запиту", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Внутрішня помилка сервера", content = @Content)
            }
    )
    public ResponseEntity<Integer> getMutualFriendsCount(
            @Parameter(description = "ID першого користувача", example = "1") @RequestParam Long userId1,
            @Parameter(description = "ID другого користувача", example = "2") @RequestParam Long userId2) {
        try {
            if (userId1 == null || userId1 <= 0 || userId2 == null || userId2 <= 0) {
                log.warn("Запит на отримання спільних друзів з некоректними ID користувачів: {} та {}", userId1, userId2);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID користувачів мають бути додатними числами");
            }

            Integer count = friendshipService.getMutualFriendsCount(userId1, userId2);
            log.info("Кількість спільних друзів між користувачами з ID {} та {} становить {}", userId1, userId2, count);
            return ResponseEntity.ok(count);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (DataAccessException e) {
            log.error("Помилка бази даних при отриманні кількості спільних друзів між користувачами {} та {}: {}",
                    userId1, userId2, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Помилка бази даних при отриманні кількості спільних друзів", e);
        } catch (Exception e) {
            log.error("Непередбачена помилка при отриманні кількості спільних друзів між користувачами {} та {}: {}",
                    userId1, userId2, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Непередбачена помилка при отриманні кількості спільних друзів", e);
        }
    }

    /**
     * Створити нову дружбу.
     * <p>
     * Цей метод обробляє POST-запити для створення нової дружби між користувачами.
     * Перед створенням виконується валідація на рівні сервісу, щоб переконатися,
     * що користувачі існують та вказаний чат є дійсним.
     * </p>
     *
     * @param friendshipEntity об'єкт дружби, що містить деталі дружби
     * @return ResponseEntity з створеним об'єктом FriendshipEntity, або з відповідним HTTP-статусом в іншому випадку:
     *         <ul>
     *             <li>201 Created - якщо дружбу успішно створено</li>
     *             <li>400 Bad Request - якщо надані дані некоректні або неповні</li>
     *             <li>409 Conflict - якщо дружба вже існує</li>
     *             <li>500 Internal Server Error - у разі непередбаченої помилки під час обробки запиту</li>
     *         </ul>
     */
    @PostMapping
    @Operation(
            summary = "Створити нову дружбу",
            description = "Створює нову дружбу між користувачами із зазначеними деталями",
            tags = {"Створення"},
            responses = {
                    @ApiResponse(responseCode = "201", description = "Дружбу успішно створено",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = FriendshipEntity.class))),
                    @ApiResponse(responseCode = "400", description = "Некоректні дані дружби або користувачі не існують", content = @Content),
                    @ApiResponse(responseCode = "409", description = "Дружба вже існує", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Внутрішня помилка сервера", content = @Content)
            }
    )
    public ResponseEntity<FriendshipEntity> createFriendship(
            @Parameter(description = "Дані для створення дружби", required = true)
            @RequestBody FriendshipEntity friendshipEntity) {
        try {
            if (friendshipEntity == null ||
                    friendshipEntity.getUserId1() == null || friendshipEntity.getUserId1() <= 0 ||
                    friendshipEntity.getUserId2() == null || friendshipEntity.getUserId2() <= 0 ||
                    friendshipEntity.getChatId() == null || friendshipEntity.getChatId() <= 0) {

                log.warn("Спроба створити дружбу з некоректними даними: {}", friendshipEntity);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Некоректні дані дружби");
            }

            if (friendshipService.existsFriendship(friendshipEntity.getUserId1(), friendshipEntity.getUserId2())) {
                log.warn("Спроба створити дружбу, яка вже існує між користувачами {} та {}",
                        friendshipEntity.getUserId1(), friendshipEntity.getUserId2());
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Дружба вже існує");
            }

            FriendshipEntity createdFriendship = friendshipService.saveFriendship(friendshipEntity);
            log.info("Дружба між користувачами {} та {} успішно створена",
                    createdFriendship.getUserId1(), createdFriendship.getUserId2());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdFriendship);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            log.error("Помилка валідації при створенні дружби: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (DataAccessException e) {
            log.error("Помилка бази даних при створенні дружби: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Помилка бази даних при створенні дружби", e);
        } catch (Exception e) {
            log.error("Непередбачена помилка при створенні дружби: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Непередбачена помилка при створенні дружби", e);
        }
    }

    /**
     * Перемкнути статус блокування дружби.
     * <p>
     * Цей метод обробляє PATCH-запити для зміни статусу блокування дружби з боку конкретного користувача.
     * Виконується перевірка, що користувач є учасником вказаної дружби, перед внесенням змін.
     * </p>
     *
     * @param id ід дружби (першого користувача)
     * @param userId ід користувача, який виконує блокування (другого користувача)
     * @return ResponseEntity з оновленим об'єктом FriendshipDTO, або з відповідним HTTP-статусом в іншому випадку:
     *         <ul>
     *             <li>200 OK - якщо статус блокування успішно змінено</li>
     *             <li>403 Forbidden - якщо користувач не є учасником дружби</li>
     *             <li>404 Not Found - якщо дружбу не знайдено</li>
     *             <li>500 Internal Server Error - у разі непередбаченої помилки під час обробки запиту</li>
     *         </ul>
     */
    @PatchMapping("/{id}/block_by/{userId}")
    @Operation(
            summary = "Змінити статус блокування",
            description = "Перемикає статус блокування дружби для вказаного користувача",
            tags = {"Оновлення"},
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Статус блокування успішно змінено",
                            content = @Content(schema = @Schema(implementation = FriendshipDTO.class))),
                    @ApiResponse(responseCode = "403",
                            description = "Користувач не є учасником цієї дружби"),
                    @ApiResponse(responseCode = "404",
                            description = "Дружбу не знайдено"),
                    @ApiResponse(responseCode = "500",
                            description = "Внутрішня помилка сервера")
            }
    )
    public ResponseEntity<FriendshipDTO> toggleBlockStatus(
            @Parameter(description = "ID дружби (першого користувача)", example = "1")
            @PathVariable Long id,
            @Parameter(description = "ID користувача, який виконує блокування", example = "2")
            @PathVariable Long userId) {
        try {
            if (id == null || id <= 0 || userId == null || userId <= 0) {
                log.warn("Запит на зміну статусу блокування з некоректними ID: id={}, userId={}", id, userId);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID користувачів мають бути додатними числами");
            }

            return friendshipService.toggleBlockStatus(id, userId)
                    .map(updatedFriendship -> {
                        log.info("Статус блокування для дружби {} користувачем {} успішно змінено", id, userId);
                        return ResponseEntity.ok(updatedFriendship);
                    })
                    .orElseGet(() -> {
                        log.warn("Дружбу з ID {} не знайдено при спробі зміни статусу блокування", id);
                        return ResponseEntity.notFound().build();
                    });
        } catch (IllegalArgumentException e) {
            log.error("Помилка валідації при зміні статусу блокування дружби {}: {}", id, e.getMessage());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage(), e);
        } catch (DataAccessException e) {
            log.error("Помилка бази даних при зміні статусу блокування дружби {}: {}", id, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Помилка бази даних при зміні статусу блокування", e);
        } catch (Exception e) {
            log.error("Непередбачена помилка при зміні статусу блокування дружби {}: {}", id, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Непередбачена помилка при зміні статусу блокування", e);
        }
    }
}
