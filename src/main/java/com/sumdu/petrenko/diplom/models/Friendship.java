package com.sumdu.petrenko.diplom.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Модель для представлення дружби між користувачами.
 * <p>
 * Цей клас використовується для зберігання інформації про дружби між користувачами в базі даних.
 * </p>
 */
@Entity
@NoArgsConstructor(force = true)
@Data
@Table(name = "friendships")
public class Friendship {
    /**
     * Унікальний ідентифікатор дружби.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique ID of a Friendship between two Users",
            examples = {"1", "100", "3197"}, requiredMode = Schema.RequiredMode.AUTO,
            accessMode = Schema.AccessMode.READ_ONLY)
    private long id = 0;

    /**
     * Користувач, який має друга.
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    @Schema(description = "The User who has a friend", requiredMode = Schema.RequiredMode.REQUIRED,
            accessMode = Schema.AccessMode.READ_ONLY)
    private User user;

    /**
     * Користувач, який є другом.
     */
    @ManyToOne
    @JoinColumn(name = "friend_id")
    @Schema(description = "The User who is a friend", requiredMode = Schema.RequiredMode.REQUIRED,
            accessMode = Schema.AccessMode.READ_ONLY)
    private User friend;

    /**
     * Конструктор для створення дружби.
     *
     * @param user   Користувач, який має друга.
     * @param friend Користувач, який є другом.
     * @throws IllegalArgumentException якщо user і friend однакові.
     */
    public Friendship(User user, User friend) {
        if (user.getId() == friend.getId()) {
            throw new IllegalArgumentException("User cannot be friends with themselves.");
        }

        if (user.getId() < friend.getId()) {
            this.user = friend;
            this.friend = user;
        } else {
            this.user = user;
            this.friend = friend;
        }
    }
}
