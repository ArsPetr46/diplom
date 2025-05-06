package com.sumdu.petrenko.diplom.microservices.friendships.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Модель для представлення дружби між користувачами.
 * <p>
 * Цей клас є сутністю JPA для зберігання інформації про дружби між користувачами в базі даних.
 * Використовується для операцій створення, зчитування, оновлення та видалення дружніх зв'язків.
 * </p>
 * <p>
 * Кожен запис дружби має унікальний складений ключ {@link FriendshipId}, який гарантує,
 * що між двома користувачами може існувати лише один зв'язок дружби. Додатково, кожна дружба
 * пов'язана з унікальним чатом через поле chatId.
 * </p>
 * <p>
 * Модель також підтримує функціональність блокування, дозволяючи кожному користувачу
 * блокувати іншого незалежно (через поля isBlockedByUser1 та isBlockedByUser2).
 * </p>
 */
@Entity
@NoArgsConstructor(force = true)
@Data
@Table(name = "friendships", schema = "friendship_service")
@Schema(description = "Сутність, що представляє дружбу між двома користувачами")
public class FriendshipEntity {
    /**
     * Складений ключ для дружби.
     * <p>
     * Цей ключ автоматично сортує ID користувачів так, щоб менший був у полі userId1,
     * а більший - у полі userId2, що забезпечує унікальність зв'язку незалежно від
     * порядку додавання користувачів у дружбу.
     * </p>
     * <p>
     * Ключ не може бути змінений після створення запису.
     * </p>
     */
    @EmbeddedId
    @Column(updatable = false)
    @NotNull(message = "ID дружби не може бути null")
    @Schema(description = "Унікальний складений ключ дружби", requiredMode = Schema.RequiredMode.REQUIRED)
    private FriendshipId id;

    /**
     * Індикатор блокування від першого користувача.
     * <p>
     * Показує, чи перший користувач (з меншим ID) заблокував другого користувача.
     * За замовчуванням встановлено значення false.
     * </p>
     */
    @Column(name = "is_blocked_by_user1", nullable = false)
    @Schema(description = "Індикатор того, чи перший користувач заблокував другого",
            example = "false", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean isBlockedByUser1 = false;

    /**
     * Індикатор блокування від другого користувача.
     * <p>
     * Показує, чи другий користувач (з більшим ID) заблокував першого користувача.
     * За замовчуванням встановлено значення false.
     * </p>
     */
    @Column(name = "is_blocked_by_user2", nullable = false)
    @Schema(description = "Індикатор того, чи другий користувач заблокував першого",
            example = "false", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean isBlockedByUser2 = false;

    /**
     * ID чату між користувачами.
     * <p>
     * Унікальний ідентифікатор чату, пов'язаного з цією дружбою.
     * Поле не може бути null, повинно бути унікальним, і не може бути змінене після створення.
     * </p>
     */
    @Column(name = "chat_id", nullable = false, unique = true, updatable = false)
    @NotNull(message = "ID чату не може бути null")
    @Schema(description = "Унікальний ID чату, пов'язаного з цією дружбою",
            example = "42", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long chatId;

    /**
     * Дата створення дружби.
     * <p>
     * Встановлюється автоматично при збереженні запису в базу даних
     * через метод життєвого циклу {@link #onCreate()}.
     * Поле не може бути null і не може бути змінене після створення.
     * </p>
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    @Schema(description = "Дата створення дружби", example = "2023-05-15",
            requiredMode = Schema.RequiredMode.AUTO, accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDate createdAt;

    /**
     * Конструктор для створення дружби між двома користувачами.
     * <p>
     * Автоматично створює об'єкт FriendshipId, який забезпечує правильне сортування ID користувачів.
     * </p>
     *
     * @param userId1 ID першого користувача
     * @param userId2 ID другого користувача
     * @param chatId ID чату між користувачами
     * @throws IllegalArgumentException якщо:
     *                                 <ul>
     *                                   <li>будь-який з ID користувачів є null</li>
     *                                   <li>обидва ID користувачів однакові</li>
     *                                   <li>ID чату є null</li>
     *                                 </ul>
     */
    public FriendshipEntity(Long userId1, Long userId2, Long chatId) {
        if (userId1 == null || userId2 == null) {
            throw new IllegalArgumentException("ID користувачів не можуть бути null");
        }

        if (Objects.equals(userId1, userId2)) {
            throw new IllegalArgumentException("ID користувачів не можуть бути однаковими");
        }

        if (chatId == null) {
            throw new IllegalArgumentException("ID чату не може бути null");
        }

        this.id = new FriendshipId(userId1, userId2);
        this.chatId = chatId;
        this.isBlockedByUser1 = false;
        this.isBlockedByUser2 = false;
    }

    /**
     * Повертає ID першого користувача (з меншим ID).
     * <p>
     * Цей метод є зручним способом отримати ID першого користувача
     * без необхідності звертатися до складового ключа.
     * </p>
     *
     * @return ID першого користувача
     * @throws NullPointerException якщо id є null
     */
    public Long getUserId1() {
        if (id == null) {
            throw new NullPointerException("ID дружби не може бути null");
        }

        return id.getUserId1();
    }

    /**
     * Повертає ID другого користувача (з більшим ID).
     * <p>
     * Цей метод є зручним способом отримати ID другого користувача
     * без необхідності звертатися до складового ключа.
     * </p>
     *
     * @return ID другого користувача
     * @throws NullPointerException якщо id є null
     */
    public Long getUserId2() {
        if (id == null) {
            throw new NullPointerException("ID дружби не може бути null");
        }

        return id.getUserId2();
    }

    /**
     * Метод життєвого циклу, який викликається перед збереженням нової дружби в базу даних.
     * <p>
     * Встановлює поточний час як час створення дружби.
     * </p>
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
    }
}
