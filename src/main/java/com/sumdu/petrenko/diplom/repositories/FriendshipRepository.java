package com.sumdu.petrenko.diplom.repositories;

import com.sumdu.petrenko.diplom.models.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторій для роботи з дружбами.
 * <p>
 * Цей інтерфейс надає методи для доступу до даних про дружби в базі даних.
 * </p>
 */
@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    /**
     * Знайти дружби за id користувача або id друга.
     *
     * @param userId   id користувача
     * @param friendId id друга
     * @return список дружб
     */
    List<Friendship> findByUserIdOrFriendId(Long userId, Long friendId);

    /**
     * Знайти дружбу за id користувача та id друга.
     *
     * @param userId1 id першого користувача
     * @param userId2 id другого користувача
     * @return дружба
     */
    Optional<Friendship> findByUserIdAndFriendId(Long userId1, Long userId2);
}