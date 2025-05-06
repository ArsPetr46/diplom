package com.sumdu.petrenko.diplom.microservices.friendships.repositories;

import com.sumdu.petrenko.diplom.microservices.friendships.models.FriendshipEntity;
import com.sumdu.petrenko.diplom.microservices.friendships.models.FriendshipId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторій для роботи з дружбами в мікросервісі управління дружбами.
 * <p>
 * Цей інтерфейс надає методи для доступу до даних про дружби в базі даних.
 * Розширює {@link JpaRepository}, що забезпечує стандартні CRUD-операції, такі як збереження,
 * отримання, оновлення та видалення записів дружб.
 * </p>
 * <p>
 * Репозиторій використовує механізм Spring Data JPA для автоматичного генерування SQL-запитів
 * на основі іменування методів або через анотацію {@link Query} з JPQL-запитами.
 * </p>
 * <p>
 * Усі методи цього інтерфейсу автоматично відкриваються в транзакції для читання (read-only),
 * якщо вони не викликаються в межах існуючої транзакції запису.
 * </p>
 *
 * @see FriendshipEntity Сутність дружби, з якою працює цей репозиторій
 * @see FriendshipId Складений ключ для ідентифікації дружби
 * @see JpaRepository Базовий інтерфейс, що надає методи для роботи з JPA-сутностями
 */
@Repository
public interface FriendshipRepository extends JpaRepository<FriendshipEntity, FriendshipId> {
    /**
     * Знаходить всі дружби, в яких бере участь вказаний користувач.
     * <p>
     * Метод виконує пошук дружб, де вказаний користувач є першим або другим учасником.
     * Це дозволяє отримати повний список друзів користувача.
     * </p>
     * <p>
     * SQL-еквівалент: {@code SELECT * FROM friendships WHERE user_id1 = ? OR user_id2 = ?}
     * </p>
     *
     * @param userId ID користувача, чиї дружби потрібно знайти (не може бути null)
     * @return список дружб, де бере участь вказаний користувач (може бути порожнім)
     * @throws org.springframework.dao.DataAccessException при помилках доступу до бази даних
     * @throws IllegalArgumentException якщо userId є null
     */
    @Query("SELECT f FROM FriendshipEntity f WHERE f.id.userId1 = :userId OR f.id.userId2 = :userId")
    List<FriendshipEntity> findByUserId(Long userId);

    /**
     * Альтернативний метод для знаходження всіх дружб, в яких бере участь вказаний користувач.
     * <p>
     * Функціонально ідентичний методу {@link #findByUserId(Long)}, але з іншою назвою.
     * Збережено для зворотної сумісності з наявним кодом.
     * </p>
     *
     * @param userId ID користувача, чиї дружби потрібно знайти (не може бути null)
     * @return список дружб, де бере участь вказаний користувач (може бути порожнім)
     * @throws org.springframework.dao.DataAccessException при помилках доступу до бази даних
     * @throws IllegalArgumentException якщо userId є null
     * @see #findByUserId(Long) Основний метод для пошуку дружб користувача
     */
    @Query("SELECT f FROM FriendshipEntity f WHERE f.id.userId1 = :userId OR f.id.userId2 = :userId")
    List<FriendshipEntity> findByUserId1OrUserId2(Long userId);

}