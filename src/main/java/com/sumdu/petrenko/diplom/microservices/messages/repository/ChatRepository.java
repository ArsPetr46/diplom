package com.sumdu.petrenko.diplom.microservices.messages.repository;

import com.sumdu.petrenko.diplom.microservices.messages.models.ChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторій для роботи з чатами в мікросервісі повідомлень.
 * <p>
 * Цей інтерфейс надає методи для доступу до даних про чати у базі даних.
 * Розширює {@link JpaRepository}, що забезпечує стандартні CRUD-операції, такі як збереження,
 * отримання, оновлення та видалення чатів.
 * </p>
 * <p>
 * Репозиторій використовує механізм Spring Data JPA для автоматичного генерування SQL-запитів,
 * що дозволяє створювати зручні методи запитів без написання SQL або JPQL коду.
 * </p>
 * <p>
 * Усі методи цього інтерфейсу автоматично відкриваються в транзакції для читання (read-only),
 * якщо вони не викликаються в межах існуючої транзакції запису.
 * </p>
 *
 * @see ChatEntity Сутність чату, з якою працює цей репозиторій
 * @see JpaRepository Базовий інтерфейс, що надає методи для роботи з JPA-сутностями
 */
@Repository
public interface ChatRepository extends JpaRepository<ChatEntity, Long> {
    /**
     * Успадковані методи від JpaRepository:
     * - save(ChatEntity): зберігає сутність чату
     * - findById(Long): знаходить чат за ідентифікатором
     * - deleteById(Long): видаляє чат за ідентифікатором
     * - existsById(Long): перевіряє існування чату за ідентифікатором
     * - findAll(): повертає всі чати
     * та інші
     */
}
