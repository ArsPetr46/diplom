package com.sumdu.petrenko.diplom.microservices.friendships.controllers;

import com.sumdu.petrenko.diplom.microservices.friendships.models.FriendRequestEntity;
import com.sumdu.petrenko.diplom.microservices.friendships.services.FriendRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Контролер для обробки запитів, пов'язаних з дружніми запитами.
 * <p>
 * Цей контролер надає API для створення, отримання та видалення дружніх запитів.
 * </p>
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/friend-requests")
@Tag(name = "FriendRequests", description = "Operations related to friend requests")
public class FriendRequestController {
    /**
     * Сервіс для роботи з дружніми запитами.
     */
    private final FriendRequestService friendRequestService;

    /**
     * Отримати дружній запит за певним id.
     *
     * @param senderId   ід відправника
     * @param receiverId ід отримувача
     * @return дружній запит
     */
    @GetMapping()
    @Operation(
            summary = "Отримати запит на дружбу за ID",
            description = "Отримує запит на дружбу за унікальним ID відправника та отримувача.",
            tags = {"Retrieve"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Запит на дружбу успішно отримано",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = FriendRequestEntity.class))),
                    @ApiResponse(responseCode = "400", description = "Некоректні дані запиту", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Запит на дружбу не знайдено", content = @Content)
            }
    )
    public ResponseEntity<FriendRequestEntity> getFriendRequestById(
            @Parameter(description = "ID відправника запиту", required = true) @RequestParam Long senderId,
            @Parameter(description = "ID отримувача запиту", required = true) @RequestParam Long receiverId) {
        try {
            Optional<FriendRequestEntity> friendRequest = friendRequestService.getFriendRequestById(senderId, receiverId);
            if (friendRequest.isPresent()) {
                log.info("Знайдено запит на дружбу від користувача {} до користувача {}", senderId, receiverId);
                return ResponseEntity.ok(friendRequest.get());
            } else {
                log.warn("Не знайдено запит на дружбу від користувача {} до користувача {}", senderId, receiverId);
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            log.error("Помилка при отриманні запиту на дружбу: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Отримати список всіх дружніх запитів, де користувач з певним id є відправником.
     *
     * @param senderId ід користувача, який надіслав дружній запит
     * @return список дружніх запитів
     */
    @GetMapping("/sender/{senderId}")
    @Operation(
            summary = "Отримати запити на дружбу за ID відправника",
            description = "Отримує список запитів на дружбу, надісланих певним користувачем.",
            tags = {"Retrieve"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Запити на дружбу успішно отримано",
                            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = FriendRequestEntity.class)))),
                    @ApiResponse(responseCode = "400", description = "Некоректні дані запиту", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Запити на дружбу не знайдено", content = @Content)
            }
    )
    public ResponseEntity<List<FriendRequestEntity>> getFriendRequestsBySenderId(
            @Parameter(description = "ID відправника, чиї запити на дружбу необхідно отримати", required = true)
            @PathVariable Long senderId) {
        try {
            List<FriendRequestEntity> friendRequestEntities = friendRequestService.getFriendRequestsBySenderId(senderId);

            if (friendRequestEntities.isEmpty()) {
                log.warn("Не знайдено запитів на дружбу від користувача з id {}", senderId);
                return ResponseEntity.notFound().build();
            }

            log.info("Знайдено {} запитів на дружбу від користувача з id {}", friendRequestEntities.size(), senderId);
            return ResponseEntity.ok(friendRequestEntities);
        } catch (IllegalArgumentException e) {
            log.error("Помилка при отриманні запитів на дружбу за ID відправника {}: {}", senderId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Отримати список всіх дружніх запитів, де користувач з певним id є отримувачем.
     *
     * @param receiverId ід користувача, який отримав дружній запит
     * @return список дружніх запитів
     */
    @GetMapping("/receiver/{receiverId}")
    @Operation(
            summary = "Отримати запити на дружбу за ID отримувача",
            description = "Отримує список запитів на дружбу, отриманих певним користувачем.",
            tags = {"Retrieve"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Запити на дружбу успішно отримано",
                            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = FriendRequestEntity.class)))),
                    @ApiResponse(responseCode = "400", description = "Некоректні дані запиту", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Запити на дружбу не знайдено", content = @Content)
            }
    )
    public ResponseEntity<List<FriendRequestEntity>> getFriendRequestsByReceiverId(
            @Parameter(description = "ID отримувача, чиї запити на дружбу необхідно отримати", required = true)
            @PathVariable Long receiverId) {
        try {
            List<FriendRequestEntity> friendRequestEntities = friendRequestService.getFriendRequestsByReceiverId(receiverId);

            if (friendRequestEntities.isEmpty()) {
                log.warn("Не знайдено запитів на дружбу для користувача з id {}", receiverId);
                return ResponseEntity.notFound().build();
            }

            log.info("Знайдено {} запитів на дружбу для користувача з id {}", friendRequestEntities.size(), receiverId);
            return ResponseEntity.ok(friendRequestEntities);
        } catch (IllegalArgumentException e) {
            log.error("Помилка при отриманні запитів на дружбу за ID отримувача {}: {}", receiverId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Перевірити, чи існує дружній запит між двома користувачами.
     *
     * @param senderId   ід відправника
     * @param receiverId ід отримувача
     * @return статус існування дружнього запиту
     */
    @GetMapping("/exists")
    @Operation(
            summary = "Перевірити існування запиту на дружбу",
            description = "Перевіряє, чи існує запит на дружбу між двома користувачами.",
            tags = {"Check"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Перевірка існування запиту на дружбу успішно виконана",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))),
                    @ApiResponse(responseCode = "400", description = "Некоректні дані запиту", content = @Content)
            }
    )
    public ResponseEntity<Boolean> existsFriendRequest(
            @Parameter(description = "ID відправника", required = true) @RequestParam Long senderId,
            @Parameter(description = "ID отримувача", required = true) @RequestParam Long receiverId) {
        try {
            boolean exists = friendRequestService.existsFriendRequest(senderId, receiverId);

            if (exists) {
                log.info("Запит на дружбу між користувачами з id {} та {} існує", senderId, receiverId);
            } else {
                log.info("Запит на дружбу між користувачами з id {} та {} не існує", senderId, receiverId);
            }
            return ResponseEntity.ok(exists);
        } catch (IllegalArgumentException e) {
            log.error("Помилка при перевірці існування запиту на дружбу: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Створити новий дружній запит.
     *
     * @param friendRequestEntity об'єкт дружнього запиту, що містить дані для створення нового запиту
     * @return створений запит на дружбу
     */
    @PostMapping
    @Operation(
            summary = "Створити новий запит на дружбу",
            description = "Створює новий запит на дружбу з наданими деталями.",
            tags = {"Create"},
            responses = {
                    @ApiResponse(responseCode = "201", description = "Запит на дружбу успішно створено",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = FriendRequestEntity.class))),
                    @ApiResponse(responseCode = "400", description = "Некоректні дані запиту на дружбу", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Внутрішня помилка сервера", content = @Content)
            }
    )
    public ResponseEntity<FriendRequestEntity> createFriendRequest(
            @Parameter(description = "Дані запиту на дружбу", required = true)
            @RequestBody FriendRequestEntity friendRequestEntity) {
        try {
            FriendRequestEntity savedRequest = friendRequestService.saveFriendRequest(friendRequestEntity);
            log.info("Створено новий запит на дружбу від користувача {} до користувача {}",
                    savedRequest.getId().getSenderId(), savedRequest.getId().getReceiverId());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedRequest);
        } catch (IllegalArgumentException e) {
            log.error("Не вдалося створити запит на дружбу: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Внутрішня помилка при створенні запиту на дружбу: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Прийняти запит на дружбу.
     *
     * @param senderId ID відправника запиту
     * @param receiverId ID отримувача запиту
     * @return статус операції
     */
    @PostMapping("/accept")
    @Operation(
            summary = "Прийняти запит на дружбу",
            description = "Приймає запит на дружбу, створюючи дружбу та видаляючи запит.",
            tags = {"Update"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Запит успішно прийнято", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Некоректні дані запиту", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Запит не знайдено", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Внутрішня помилка сервера", content = @Content)
            }
    )
    public ResponseEntity<Void> acceptFriendRequest(
            @Parameter(description = "ID відправника запиту", required = true) @RequestParam Long senderId,
            @Parameter(description = "ID отримувача запиту", required = true) @RequestParam Long receiverId) {
        try {
            friendRequestService.acceptFriendRequest(senderId, receiverId);
            log.info("Запит на дружбу від користувача {} до користувача {} успішно прийнято", senderId, receiverId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.warn("Не вдалося прийняти запит на дружбу: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            log.error("Помилка при прийнятті запиту на дружбу: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Відхилити запит на дружбу.
     *
     * @param senderId ID відправника запиту
     * @param receiverId ID отримувача запиту
     * @return статус операції
     */
    @PostMapping("/reject")
    @Operation(
            summary = "Відхилити запит на дружбу",
            description = "Відхиляє запит на дружбу, видаляючи його без створення дружби.",
            tags = {"Update"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Запит успішно відхилено", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Некоректні дані запиту", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Запит не знайдено", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Внутрішня помилка сервера", content = @Content)
            }
    )
    public ResponseEntity<Void> rejectFriendRequest(
            @Parameter(description = "ID відправника запиту", required = true) @RequestParam Long senderId,
            @Parameter(description = "ID отримувача запиту", required = true) @RequestParam Long receiverId) {
        try {
            friendRequestService.rejectFriendRequest(senderId, receiverId);
            log.info("Запит на дружбу від користувача {} до користувача {} успішно відхилено", senderId, receiverId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.warn("Не вдалося відхилити запит на дружбу: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            log.error("Помилка при відхиленні запиту на дружбу: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
