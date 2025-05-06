package com.sumdu.petrenko.diplom.microservices.messages.controllers;

import com.sumdu.petrenko.diplom.microservices.messages.models.ChatEntity;
import com.sumdu.petrenko.diplom.microservices.messages.services.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Контролер для обробки запитів, пов'язаних з чатами.
 * <p>
 * Цей контролер є частиною мікросервісу повідомлень і надає RESTful API для створення,
 * перевірки та видалення чатів. Він обробляє HTTP-запити, що стосуються
 * сутностей чатів, та передає їх до відповідного сервісу для виконання бізнес-логіки.
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
 *   <li>@RequestMapping("/api/chats") - визначає базовий URL для всіх методів контролера</li>
 *   <li>@Tag - додає тег для документації Swagger, що групує API-методи</li>
 * </ul>
 * </p>
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/chats")
@Tag(name = "Чати", description = "Операції з чатами")
public class ChatController {
    /**
     * Сервіс для роботи з чатами.
     * <p>
     * Цей сервіс надає методи для виконання операцій з чатами, такі як перевірка існування, створення
     * та видалення. Він інжектується через конструктор завдяки анотації @RequiredArgsConstructor.
     * </p>
     * <p>
     * Взаємодіє з репозиторієм даних для виконання операцій з базою даних та реалізує бізнес-логіку,
     * пов'язану з чатами.
     * </p>
     */
    private final ChatService chatService;

    /**
     * Перевіряє, чи існує чат за його id.
     * <p>
     * Цей метод використовується для швидкої перевірки існування чату без необхідності
     * отримувати всі його дані. Може використовуватися іншими мікросервісами для валідації
     * посилань на чати.
     * </p>
     *
     * @param id Ідентифікатор чату, якого потрібно перевірити
     * @return ResponseEntity з результатом перевірки та відповідним HTTP-статусом:
     *         <ul>
     *             <li>200 OK - з результатом перевірки (true або false)</li>
     *             <li>400 Bad Request - якщо id некоректний (null, від'ємний або нуль)</li>
     *             <li>500 Internal Server Error - у разі непередбаченої помилки бази даних</li>
     *         </ul>
     */
    @GetMapping("/{id}/exists")
    @Operation(
            summary = "Перевірити існування чату",
            description = "Перевіряє чи існує чат із зазначеним id. Повертає true, якщо чат існує, або false, якщо чату немає.",
            tags = {"Перевірка"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Перевірка успішно виконана",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))),
                    @ApiResponse(responseCode = "400", description = "Неправильний формат id (від'ємне або нуль)", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Помилка сервера при виконанні запиту", content = @Content)
            }
    )
    public ResponseEntity<Boolean> chatExists(
            @Parameter(description = "Id чату", example = "42")
            @PathVariable Long id) {
        log.debug("Отримано запит на перевірку існування чату з ID: {}", id);

        try {
            boolean exists = chatService.chatExists(id);
            log.debug("Чат з ID {} {}", id, exists ? "існує" : "не існує");
            return ResponseEntity.ok(exists);
        } catch (IllegalArgumentException e) {
            log.error("Некоректний ID чату: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (   DataAccessException e) {
            log.error("Помилка доступу до бази даних при перевірці існування чату з ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            log.error("Непередбачена помилка при перевірці існування чату з ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Створює новий чат.
     * <p>
     * Цей метод обробляє POST-запити для створення нового чату в системі.
     * Виконує валідацію об'єкта чату перед створенням.
     * </p>
     *
     * @param chatEntity Сутність чату для створення
     * @return ResponseEntity зі створеною сутністю чату та відповідним HTTP-статусом:
     *         <ul>
     *             <li>201 Created - з даними створеного чату</li>
     *             <li>400 Bad Request - якщо дані чату некоректні</li>
     *             <li>500 Internal Server Error - у разі непередбаченої помилки бази даних</li>
     *         </ul>
     */
    @PostMapping
    @Operation(
            summary = "Створити новий чат",
            description = "Створює новий чат в системі. Валідує дані чату перед створенням.",
            tags = {"Створення"},
            responses = {
                    @ApiResponse(responseCode = "201", description = "Чат успішно створено",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChatEntity.class))),
                    @ApiResponse(responseCode = "400", description = "Неправильні дані чату", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Помилка сервера при створенні чату", content = @Content)
            }
    )
    public ResponseEntity<ChatEntity> createChat(
            @Parameter(description = "Дані чату для створення")
            @RequestBody @Valid ChatEntity chatEntity) {
        log.debug("Отримано запит на створення нового чату");

        try {
            ChatEntity createdChat = chatService.createChat(chatEntity);
            log.info("Успішно створено чат з ID: {}", createdChat.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdChat);
        } catch (IllegalArgumentException e) {
            log.error("Некоректні дані чату: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (DataAccessException e) {
            log.error("Помилка доступу до бази даних при створенні чату: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            log.error("Непередбачена помилка при створенні чату: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Видаляє чат за його id.
     * <p>
     * Цей метод обробляє DELETE-запити для видалення чату з системи.
     * Перед видаленням перевіряється існування чату за вказаним id.
     * </p>
     *
     * @param id Ідентифікатор чату для видалення
     * @return ResponseEntity з відповідним HTTP-статусом:
     *         <ul>
     *             <li>204 No Content - якщо чат успішно видалено</li>
     *             <li>400 Bad Request - якщо id некоректний (null, від'ємний або нуль)</li>
     *             <li>404 Not Found - якщо чат з вказаним id не знайдено</li>
     *             <li>500 Internal Server Error - у разі непередбаченої помилки бази даних</li>
     *         </ul>
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Видалити чат",
            description = "Видаляє чат за вказаним id. Перевіряє існування чату перед видаленням.",
            tags = {"Видалення"},
            responses = {
                    @ApiResponse(responseCode = "204", description = "Чат успішно видалено", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Неправильний формат id (від'ємне або нуль)", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Чат з вказаним id не знайдено", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Помилка сервера при видаленні чату", content = @Content)
            }
    )
    public ResponseEntity<Void> deleteChat(
            @Parameter(description = "Id чату", example = "42")
            @PathVariable Long id) {
        log.debug("Отримано запит на видалення чату з ID: {}", id);

        try {
            chatService.deleteChat(id);
            log.info("Чат з ID {} успішно видалено", id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("Некоректний ID чату: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (EntityNotFoundException e) {
            log.error("Чат з ID {} не знайдено", id);
            return ResponseEntity.notFound().build();
        } catch (DataAccessException e) {
            log.error("Помилка доступу до бази даних при видаленні чату з ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            log.error("Непередбачена помилка при видаленні чату з ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

