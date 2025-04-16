package com.sumdu.petrenko.diplom.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Модель для представлення запиту на дружбу між користувачами.
 * <p>
 * Цей клас використовується для зберігання інформації про запити на дружбу між користувачами в базі даних.
 * </p>
 */
@Entity
@NoArgsConstructor(force = true)
@Data
@Table(name = "friend_requests")
public class FriendRequest {
    /**
     * Унікальний ідентифікатор запиту на дружбу.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique ID of a Friend Request between two Users",
            examples = {"1", "100", "3197"}, requiredMode = Schema.RequiredMode.AUTO,
            accessMode = Schema.AccessMode.READ_ONLY)
    private long id = 0;

    /**
     * Користувач, який надіслав запит на дружбу.
     */
    @ManyToOne
    @JoinColumn(name = "sender_id")
    @Schema(description = "The User who has sent a Friend Request", requiredMode = Schema.RequiredMode.REQUIRED,
            accessMode = Schema.AccessMode.READ_ONLY)
    private User sender;

    /**
     * Користувач, якому надіслано запит на дружбу.
     */
    @ManyToOne
    @JoinColumn(name = "receiver_id")
    @Schema(description = "The User who has to answer a Friend Request", requiredMode = Schema.RequiredMode.REQUIRED,
            accessMode = Schema.AccessMode.READ_ONLY)
    private User receiver;

    /**
     * Конструктор для створення запиту на дружбу.
     *
     * @param sender   Користувач, який надіслав запит на дружбу.
     * @param receiver Користувач, якому надіслано запит на дружбу.
     * @throws IllegalArgumentException якщо sender і receiver однакові.
     */
    public FriendRequest(User sender, User receiver) {
        if (sender.getId() == receiver.getId()) {
            throw new IllegalArgumentException("Sender cannot send a friend request to themselves.");
        }

        if (sender.getId() < receiver.getId()) {
            this.sender = receiver;
            this.receiver = sender;
        } else {
            this.sender = sender;
            this.receiver = receiver;
        }
    }
}
