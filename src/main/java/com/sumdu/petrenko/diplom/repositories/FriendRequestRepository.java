package com.sumdu.petrenko.diplom.repositories;

import com.sumdu.petrenko.diplom.models.FriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторій для роботи з запитами на дружбу.
 * <p>
 * Цей інтерфейс надає методи для доступу до даних про запити на дружбу в базі даних.
 * </p>
 */
@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    /**
     * Знайти запити на дружбу за id відправника.
     *
     * @param senderId id відправника
     * @return список запитів на дружбу
     */
    List<FriendRequest> findBySenderId(Long senderId);

    /**
     * Знайти запити на дружбу за id отримувача.
     *
     * @param receiverId id отримувача
     * @return список запитів на дружбу
     */
    List<FriendRequest> findByReceiverId(Long receiverId);

    /**
     * Знайти запит на дружбу за id відправника та отримувача.
     *
     * @param senderId   id відправника
     * @param receiverId id отримувача
     * @return запит на дружбу
     */
    Optional<FriendRequest> findBySenderIdAndReceiverId(Long senderId, Long receiverId);
}
