package com.sumdu.petrenko.diplom.microservices.messages.controllers;

import com.sumdu.petrenko.diplom.microservices.messages.models.MessageEntity;
import com.sumdu.petrenko.diplom.microservices.messages.services.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контролер для роботи з повідомленнями.
 * <p>
 * Цей контролер надає REST API для створення, читання, оновлення та видалення повідомлень.
 * </p>
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/messages")
@Tag(name = "Повідомлення", description = "API для роботи з повідомленнями")
public class MessageController {

    private final MessageService messageService;

    /**
     * Отримання повідомлення за ідентифікатором.
     *
     * @param messageId ідентифікатор повідомлення
     * @return повідомлення
     */
    @GetMapping("/{messageId}")
    @Operation(
            summary = "Отримати повідомлення за ID",
            description = "Дозволяє отримати детальну інформацію про конкретне повідомлення за його ідентифікатором",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успішне отримання повідомлення",
                            content = @Content(schema = @Schema(implementation = MessageEntity.class))),
                    @ApiResponse(responseCode = "404", description = "Повідомлення не знайдено")
            }
    )
    public ResponseEntity<MessageEntity> getMessageById(
            @Parameter(description = "ID повідомлення") @PathVariable Long messageId) {
        return messageService.getMessageById(messageId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Отримання списку останніх повідомлень для конкретного чату.
     *
     * @param chatId ідентифікатор чату
     * @param limit  кількість повідомлень для отримання (за замовчуванням 20)
     * @return список повідомлень
     */
    @GetMapping("/chat/{chatId}")
    @Operation(
            summary = "Отримати останні повідомлення чату",
            description = "Дозволяє отримати останні повідомлення для конкретного чату з можливістю обмеження кількості",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успішне отримання повідомлень"),
                    @ApiResponse(responseCode = "404", description = "Чат не знайдено")
            }
    )
    public ResponseEntity<List<MessageEntity>> getLatestChatMessages(
            @Parameter(description = "ID чату") @PathVariable Long chatId,
            @Parameter(description = "Кількість повідомлень (макс. 100)") @RequestParam(defaultValue = "20") int limit,
            @Parameter(description = "Id крайнього ковідомлення") @RequestParam(required = false) Long beforeMessageId) {
        if (limit > 100) {
            limit = 100;
        }

        List<MessageEntity> messages;
        if (beforeMessageId == null) {
            messages = messageService.getLatestChatMessages(chatId, limit);
        } else {
            messages = messageService.getLatestChatMessagesBeforeId(
                    chatId, limit, beforeMessageId);
        }

        return ResponseEntity.ok(messages);
    }

    /**
     * Створення нового повідомлення.
     *
     * @param messageEntity дані повідомлення
     * @return створене повідомлення
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Створити нове повідомлення",
            description = "Дозволяє створити нове повідомлення в чаті",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Повідомлення успішно створено",
                            content = @Content(schema = @Schema(implementation = MessageEntity.class))),
                    @ApiResponse(responseCode = "400", description = "Неправильні дані повідомлення")
            }
    )
    public ResponseEntity<MessageEntity> createMessage(
            @Parameter(description = "Дані повідомлення") @RequestBody @Valid MessageEntity messageEntity) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(messageService.createMessage(messageEntity));
    }

    /**
     * Оновлення існуючого повідомлення.
     *
     * @param messageId     ідентифікатор повідомлення
     * @param messageEntity нові дані повідомлення
     * @return оновлене повідомлення
     */
    @PutMapping("/{messageId}")
    @Operation(
            summary = "Оновити повідомлення",
            description = "Дозволяє оновити текст існуючого повідомлення",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Повідомлення успішно оновлено",
                            content = @Content(schema = @Schema(implementation = MessageEntity.class))),
                    @ApiResponse(responseCode = "400", description = "Неправильні дані повідомлення"),
                    @ApiResponse(responseCode = "404", description = "Повідомлення не знайдено")
            }
    )
    public ResponseEntity<MessageEntity> updateMessage(
            @Parameter(description = "ID повідомлення") @PathVariable Long messageId,
            @Parameter(description = "Оновлені дані повідомлення") @RequestBody @Valid MessageEntity messageEntity) {
        return messageService.updateMessage(messageId, messageEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Видалення повідомлення.
     *
     * @param messageId ідентифікатор повідомлення
     * @return статус операції
     */
    @DeleteMapping("/{messageId}")
    @Operation(
            summary = "Видалити повідомлення",
            description = "Дозволяє видалити повідомлення за його ідентифікатором",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Повідомлення успішно видалено"),
                    @ApiResponse(responseCode = "404", description = "Повідомлення не знайдено")
            }
    )
    public ResponseEntity<Void> deleteMessage(
            @Parameter(description = "ID повідомлення") @PathVariable Long messageId) {
        if (messageService.deleteMessage(messageId)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

