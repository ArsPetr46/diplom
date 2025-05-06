package com.sumdu.petrenko.diplom.microservices.users.repositories;

import com.sumdu.petrenko.diplom.microservices.users.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Репозиторій для роботи з користувачами в мікросервісі управління користувачами.
 * <p>
 * Цей інтерфейс надає методи для доступу до даних про користувачів у базі даних PostgreSQL.
 * Розширює {@link JpaRepository}, що забезпечує стандартні CRUD-операції, такі як збереження,
 * отримання, оновлення та видалення записів користувачів.
 * </p>
 * <p>
 * Репозиторій використовує механізм Spring Data JPA для автоматичного генерування SQL-запитів
 * на основі іменування методів, що дозволяє створювати зручні методи запитів без написання
 * SQL або JPQL коду.
 * </p>
 * <p>
 * Усі методи цього інтерфейсу автоматично відкриваються в транзакції для читання (read-only),
 * якщо вони не викликаються в межах існуючої транзакції запису.
 * </p>
 *
 * @see UserEntity Сутність користувача, з якою працює цей репозиторій
 * @see JpaRepository Базовий інтерфейс, що надає методи для роботи з JPA-сутностями
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    /**
     * Перевіряє, чи існує користувач з вказаним нікнеймом.
     * <p>
     * Метод використовується для перевірки унікальності нікнейму при реєстрації нових користувачів
     * або оновленні даних існуючих користувачів. Запит оптимізований для швидкої перевірки
     * наявності запису без завантаження повних даних сутності.
     * </p>
     * <p>
     * SQL-еквівалент: {@code SELECT EXISTS(SELECT 1 FROM users WHERE nickname = ?)}
     * </p>
     *
     * @param nickname нікнейм користувача для перевірки (не може бути null)
     * @return {@code true}, якщо користувач з таким нікнеймом існує, {@code false} - якщо не існує
     * @throws org.springframework.dao.DataAccessException при помилках доступу до бази даних
     * @throws IllegalArgumentException якщо nickname є null
     */
    Boolean existsByNickname(String nickname);

    /**
     * Перевіряє, чи існує користувач з вказаною електронною поштою.
     * <p>
     * Метод використовується для перевірки унікальності email при реєстрації нових користувачів.
     * Запит оптимізований для швидкої перевірки наявності запису без завантаження повних даних сутності.
     * </p>
     * <p>
     * SQL-еквівалент: {@code SELECT EXISTS(SELECT 1 FROM users WHERE email = ?)}
     * </p>
     * <p>
     * У зв'язку з тим, що в моделі {@link UserEntity} поле email має обмеження {@code updatable = false},
     * цей метод не використовується при оновленні даних користувачів.
     * </p>
     *
     * @param email електронна пошта користувача для перевірки (не може бути null)
     * @return {@code true}, якщо користувач з такою електронною поштою існує, {@code false} - якщо не існує
     * @throws org.springframework.dao.DataAccessException при помилках доступу до бази даних
     * @throws IllegalArgumentException якщо email є null
     */
    Boolean existsByEmail(String email);

    /**
     * Знаходить всіх користувачів, нікнейми яких містять вказаний підрядок (без урахування регістру).
     * <p>
     * Метод використовується для пошуку користувачів за частковим збігом нікнейму.
     * Пошук не чутливий до регістру, тобто пошуковий запит "user" знайде користувачів з нікнеймами
     * "User123", "SUPERUSER", "MyUserName" тощо.
     * </p>
     * <p>
     * SQL-еквівалент: {@code SELECT * FROM users WHERE LOWER(nickname) LIKE LOWER('%' || ? || '%')}
     * </p>
     * <p>
     * Результати не обмежуються за кількістю, тому при великій кількості збігів можуть виникнути
     * проблеми з продуктивністю. Для запитів з потенційно великою кількістю результатів
     * рекомендується використовувати пагінацію.
     * </p>
     *
     * @param nickname підрядок для пошуку в нікнеймах користувачів (не може бути null)
     * @return список користувачів, чиї нікнейми містять вказаний підрядок (може бути порожнім)
     * @throws org.springframework.dao.DataAccessException при помилках доступу до бази даних
     * @throws IllegalArgumentException якщо nickname є null
     */
    List<UserEntity> findByNicknameContainingIgnoreCase(String nickname);
}
