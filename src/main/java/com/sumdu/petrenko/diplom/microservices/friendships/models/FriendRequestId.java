package com.sumdu.petrenko.diplom.microservices.friendships.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Складений ключ для запиту на дружбу між користувачами.
 * <p>
 * Цей клас представляє складений ключ, що ідентифікує унікальний запит на дружбу
 * між двома користувачами системи. Кожен FriendRequestId складається з:
 * <ul>
 *   <li>senderId - ідентифікатор користувача, який надіслав запит</li>
 *   <li>receiverId - ідентифікатор користувача, який отримав запит</li>
 * </ul>
 * </p>
 * <p>
 * Ключ використовується як унікальний ідентифікатор для {@link FriendRequestEntity}.
 * </p>
 */
@Embeddable
@Data
@NoArgsConstructor(force = true)
public class FriendRequestId implements Serializable {
    /**
     * ID користувача, який надіслав запит на дружбу.
     * <p>
     * Це поле не може бути null та не може бути змінене після створення.
     * </p>
     */
    @Column(name = "sender_id", nullable = false, updatable = false)
    private final Long senderId;

    /**
     * ID користувача, який отримав запит на дружбу.
     * <p>
     * Це поле не може бути null та не може бути змінене після створення.
     * </p>
     */
    @Column(name = "receiver_id", nullable = false, updatable = false)
    private final Long receiverId;

    /**
     * Створює новий складений ключ запиту на дружбу.
     *
     * @param senderId ID користувача, який надіслав запит на дружбу
     * @param receiverId ID користувача, який отримав запит на дружбу
     * @throws IllegalArgumentException якщо будь-який з ID є null або якщо це той самий користувач
     */
    public FriendRequestId(Long senderId, Long receiverId) {
        if (senderId == null || receiverId == null) {
            throw new IllegalArgumentException("ID користувачів не можуть бути null");
        }
        if (senderId.equals(receiverId)) {
            throw new IllegalArgumentException("Користувач не може бути другом самого себе");
        }

        this.senderId = senderId;
        this.receiverId = receiverId;
    }
}
