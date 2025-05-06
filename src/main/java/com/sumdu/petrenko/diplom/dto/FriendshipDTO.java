package com.sumdu.petrenko.diplom.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO для представлення зв'язку дружби між користувачами.
 * <p>
 * Цей клас надає інформацію про дружбу з точки зору конкретного користувача,
 * включаючи статус блокування та дату створення. DTO адаптує двонаправлений зв'язок
 * дружби до односторонньої перспективи поточного користувача.
 * </p>
 * <p>
 * Використовується в API для надання даних про друзів користувача та статус
 * їхніх відносин.
 * </p>
 *
 * @see com.sumdu.petrenko.diplom.microservices.friendships.models.FriendshipEntity Сутність дружби
 * @see com.sumdu.petrenko.diplom.mappers.FriendshipMapper Маппер для конвертації сутності в DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Schema(description = "Інформація про дружбу з точки зору конкретного користувача")
public class FriendshipDTO {
    /**
     * Ідентифікатор користувача-друга.
     * Представляє другу сторону відносин дружби.
     */
    @Schema(description = "Id друга")
    private final Long friendId;

    /**
     * Прапорець, що вказує чи поточний користувач заблокував друга.
     * Якщо true, поточний користувач не отримує повідомлення та оновлення від друга.
     */
    @Schema(description = "Чи заблокована дружба з боку користувача")
    private final boolean isBlockedByUser;

    /**
     * Прапорець, що вказує чи друг заблокував поточного користувача.
     * Якщо true, друг не отримує повідомлення та оновлення від поточного користувача.
     */
    @Schema(description = "Чи заблокована дружба з боку друга")
    private final boolean isBlockedByFriend;

    /**
     * Дата створення дружби між користувачами.
     * Встановлюється при прийнятті запиту на дружбу.
     */
    @Schema(description = "Дата створення дружби")
    private final LocalDate createdAt;
}
