package com.sumdu.petrenko.diplom.microservices.friendships.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Складений ключ для дружби між користувачами.
 * <p>
 * Цей клас представляє унікальний ідентифікатор для зв'язку дружби між двома користувачами.
 * Для забезпечення унікальності та запобігання дублювання записів (коли userId1 та userId2 міняються місцями),
 * клас автоматично впорядковує ідентифікатори користувачів: менший ID завжди зберігається у полі userId1,
 * а більший ID - у полі userId2.
 * </p>
 * <p>
 * Клас має власні механізми валідації, щоб забезпечити, що:
 * <ul>
 *   <li>Обидва ID користувачів не можуть бути null</li>
 *   <li>Обидва ID користувачів не можуть бути однаковими (користувач не може дружити сам із собою)</li>
 * </ul>
 * </p>
 */
@Embeddable
@Data
@NoArgsConstructor(force = true)
@Schema(description = "Складений ключ для дружби між користувачами")
public class FriendshipId implements Serializable {
    /**
     * ID першого користувача (завжди менший з двох ID).
     * <p>
     * Це поле завжди містить менше значення з двох ID користувачів,
     * незалежно від того, в якому порядку вони були передані в конструктор.
     * Поле не може бути null та не може бути змінене після створення.
     * </p>
     */
    @Column(name = "user_id1", nullable = false, updatable = false)
    @Schema(description = "ID першого користувача (завжди менший з двох ID)",
            example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private final Long userId1;

    /**
     * ID другого користувача (завжди більший з двох ID).
     * <p>
     * Це поле завжди містить більше значення з двох ID користувачів,
     * незалежно від того, в якому порядку вони були передані в конструктор.
     * Поле не може бути null та не може бути змінене після створення.
     * </p>
     */
    @Column(name = "user_id2", nullable = false, updatable = false)
    @Schema(description = "ID другого користувача (завжди більший з двох ID)",
            example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    private final Long userId2;

    /**
     * Конструктор для створення складаного ключа дружби.
     * <p>
     * Автоматично сортує ID користувачів так, щоб менший був у полі userId1,
     * а більший - у полі userId2. Це гарантує унікальність ключа незалежно від порядку
     * передачі ID.
     * </p>
     *
     * @param userId1 ID першого користувача
     * @param userId2 ID другого користувача
     * @throws IllegalArgumentException якщо:
     *                                 <ul>
     *                                   <li>будь-який з ID є null</li>
     *                                   <li>обидва ID однакові (користувач не може дружити сам із собою)</li>
     *                                 </ul>
     */
    public FriendshipId(Long userId1, Long userId2) {
        if (userId1 == null || userId2 == null) {
            throw new IllegalArgumentException("ID користувачів не можуть бути null");
        }
        if (userId1.equals(userId2)) {
            throw new IllegalArgumentException("Користувач не може бути другом самого себе");
        }

        if (userId1 < userId2) {
            this.userId1 = userId1;
            this.userId2 = userId2;
        } else {
            this.userId1 = userId2;
            this.userId2 = userId1;
        }
    }
}
