package com.sumdu.petrenko.diplom.repositories;

import com.sumdu.petrenko.diplom.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Репозиторій для роботи з користувачами.
 * <p>
 * Цей інтерфейс надає методи для доступу до даних про користувачів в базі даних.
 * </p>
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Знайти користувача за його іменем.
     *
     * @param nickname ім'я користувача
     * @return список користувачів з таким ім'ям
     */
    List<User> findByNicknameContainingIgnoreCase(String nickname);
}
