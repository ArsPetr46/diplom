package com.sumdu.petrenko.diplom.microservices.friendships.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.time.LocalDateTime;

/**
 * Модель для представлення запиту на дружбу між користувачами.
 * <p>
 * Цей клас представляє запит на дружбу, надісланий від одного користувача до іншого.
 * Кожен запит однозначно ідентифікується складеним ключем {@link FriendRequestId},
 * який складається з ID відправника та отримувача.
 * </p>
 * <p>
 * Запит на дружбу має наступні характеристики:
 * <ul>
 *   <li>складений ключ, що містить ID відправника та отримувача</li>
 *   <li>час створення запиту</li>
 * </ul>
 * </p>
 * <p>
 * Сутність помічена як {@code @Immutable}, що означає, що після створення
 * її атрибути не можуть бути змінені.
 * </p>
 */
@Entity
@Immutable
@NoArgsConstructor(force = true)
@Data
@Table(name = "friend_requests", schema = "friendship_service")
public class FriendRequestEntity {
    /**
     * Складений ключ для запиту на дружбу.
     * <p>
     * Цей ключ однозначно ідентифікує запит на дружбу та містить ID
     * відправника та отримувача.
     * </p>
     */
    @EmbeddedId
    private FriendRequestId id;

    /**
     * Час створення запиту.
     * <p>
     * Це поле встановлюється автоматично при збереженні сутності в базу даних.
     * </p>
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Конструктор для створення запиту на дружбу.
     *
     * @param senderId   ID користувача, який надіслав запит на дружбу
     * @param receiverId ID користувача, якому надіслано запит на дружбу
     * @throws IllegalArgumentException якщо ID відправника або отримувача є null,
     *                                  або якщо відправник і отримувач однакові
     */
    public FriendRequestEntity(Long senderId, Long receiverId) {
        if (senderId == null || receiverId == null) {
            throw new IllegalArgumentException("ID відправника та отримувача не можуть бути null");
        }
        if (senderId.equals(receiverId)) {
            throw new IllegalArgumentException("Відправник не може надіслати запит самому собі");
        }

        this.id = new FriendRequestId(senderId, receiverId);
    }

    /**
     * Встановлює час створення запиту на дружбу.
     * <p>
     * Викликається автоматично перед збереженням сутності в базу даних.
     * </p>
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
