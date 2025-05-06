package com.sumdu.petrenko.diplom.microservices.friendships.repositories;

import com.sumdu.petrenko.diplom.microservices.friendships.models.FriendRequestEntity;
import com.sumdu.petrenko.diplom.microservices.friendships.models.FriendRequestId;
import com.sumdu.petrenko.diplom.microservices.friendships.models.FriendshipEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторій для роботи з запитами на дружбу.
 * <p>
 * Цей інтерфейс надає методи для доступу до даних про запити на дружбу в базі даних.
 * Розширює {@link JpaRepository}, що надає стандартні методи CRUD та пагінацію.
 * </p>
 * <p>
 * Основні операції включають:
 * <ul>
 *   <li>знаходження запитів за ID відправника</li>
 *   <li>знаходження запитів за ID отримувача</li>
 *   <li>базові операції CRUD над сутністю {@link FriendRequestEntity}</li>
 * </ul>
 * </p>
 */
@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequestEntity, FriendRequestId> {
    /**
     * Знаходить всі запити на дружбу, надіслані конкретним користувачем.
     *
     * @param senderId ID користувача-відправника
     * @return список запитів на дружбу, надісланих вказаним користувачем
     */
    @Query("SELECT f FROM FriendRequestEntity f WHERE f.id.senderId = :senderId")
    List<FriendRequestEntity> findBySenderId(Long senderId);

    /**
     * Знаходить всі запити на дружбу, отримані конкретним користувачем.
     *
     * @param receiverId ID користувача-отримувача
     * @return список запитів на дружбу, отриманих вказаним користувачем
     */
    @Query("SELECT f FROM FriendRequestEntity f WHERE f.id.receiverId = :receiverId")
    List<FriendRequestEntity> findByReceiverId(Long receiverId);
}
