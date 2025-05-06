package com.sumdu.petrenko.diplom.microservices.messages.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Модель для представлення чату.
 * <p>
 * Цей клас є сутністю JPA для зберігання інформації про чати у базі даних.
 * Використовується для операцій створення, зчитування, оновлення та видалення чатів.
 * </p>
 * <p>
 * Сутність чату є центральним елементом для організації обміну повідомленнями
 * між користувачами в системі.
 * </p>
 */
@Entity
@NoArgsConstructor(force = true)
@Data
@Table(name = "chats", schema = "message_service")
public class ChatEntity {
    /**
     * Унікальний ідентифікатор чату.
     * <p>
     * Генерується автоматично при збереженні в базу даних.
     * Не може бути змінений після створення.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    @Schema(description = "Унікальний ID чату",
            examples = {"1", "42", "100"},
            requiredMode = Schema.RequiredMode.AUTO,
            accessMode = Schema.AccessMode.READ_ONLY)
    private long id;
}
