package com.sumdu.petrenko.diplom.microservices.messages.services;

import com.sumdu.petrenko.diplom.microservices.messages.models.ChatEntity;
import com.sumdu.petrenko.diplom.microservices.messages.repository.ChatRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Сервіс для роботи з чатами.
 * <p>
 * Цей клас надає методи для обробки чатів, включаючи їх створення, видалення та перевірку існування.
 * Реалізує бізнес-логіку для управління сутностями чатів.
 * </p>
 * <p>
 * Сервіс взаємодіє з репозиторієм для виконання операцій з базою даних. Методи, що модифікують дані,
 * позначені анотацією {@code @Transactional} для забезпечення атомарності операцій.
 * </p>
 * <p>
 * Реалізує паттерн "Фасад" для надання спрощеного інтерфейсу до підсистеми управління чатами.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {
    /**
     * Репозиторій для роботи з чатами.
     * <p>
     * Забезпечує доступ до даних чатів у базі даних через JPA інтерфейс.
     * Надає методи для виконання базових CRUD-операцій з чатами.
     * </p>
     */
    private final ChatRepository chatRepository;

    /**
     * Перевіряє, чи існує чат з вказаним ідентифікатором.
     * <p>
     * Метод використовується для швидкої перевірки існування чату без необхідності
     * отримувати всі його дані. Може використовуватися іншими мікросервісами для валідації
     * посилань на чати.
     * </p>
     *
     * @param id ідентифікатор чату
     * @return true, якщо чат існує, false - якщо ні
     * @throws IllegalArgumentException якщо id є null, від'ємним або нульовим
     * @throws DataAccessException при помилках доступу до бази даних
     */
    public boolean chatExists(Long id) {
        if (id == null) {
            log.error("Спроба перевірки існування чату з null ідентифікатором");
            throw new IllegalArgumentException("Ідентифікатор чату не може бути null");
        }

        if (id <= 0) {
            log.error("Спроба перевірки існування чату з некоректним ідентифікатором: {}", id);
            throw new IllegalArgumentException("Ідентифікатор чату повинен бути додатним числом");
        }

        try {
            boolean exists = chatRepository.existsById(id);
            log.debug("Перевірка існування чату з ID {}: {}", id, exists);
            return exists;
        } catch (DataAccessException e) {
            log.error("Помилка доступу до бази даних при перевірці існування чату з ID {}: {}", id, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Непередбачена помилка при перевірці існування чату з ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Помилка перевірки існування чату", e);
        }
    }

    /**
     * Створює новий чат у системі.
     * <p>
     * Метод зберігає надану сутність чату в базі даних, присвоюючи їй унікальний ідентифікатор.
     * </p>
     * <p>
     * Метод виконується в межах транзакції для забезпечення атомарності операції.
     * </p>
     *
     * @param chatEntity сутність чату для створення
     * @return створена сутність чату з присвоєним ID
     * @throws IllegalArgumentException якщо chatEntity є null
     * @throws DataAccessException при помилках доступу до бази даних
     */
    @Transactional
    public ChatEntity createChat(ChatEntity chatEntity) {
        if (chatEntity == null) {
            log.error("Спроба створення null чату");
            throw new IllegalArgumentException("Сутність чату не може бути null");
        }

        try {
            ChatEntity savedChat = chatRepository.save(chatEntity);
            log.info("Чат успішно створено з ID: {}", savedChat.getId());
            return savedChat;
        } catch (DataAccessException e) {
            log.error("Помилка доступу до бази даних при створенні чату: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Непередбачена помилка при створенні чату: {}", e.getMessage(), e);
            throw new RuntimeException("Помилка створення чату", e);
        }
    }

    /**
     * Видаляє чат за його ідентифікатором.
     * <p>
     * Метод видаляє чат з системи за його ідентифікатором. Операція незворотна.
     * Перед видаленням перевіряється існування чату з вказаним ID.
     * </p>
     * <p>
     * Метод виконується в межах транзакції для забезпечення атомарності операції.
     * Якщо з чатом пов'язані інші дані (наприклад, повідомлення), може виникнути помилка обмеження цілісності.
     * </p>
     *
     * @param id ідентифікатор чату для видалення
     * @throws IllegalArgumentException якщо id є null, від'ємним або нульовим
     * @throws EntityNotFoundException якщо чат з вказаним id не знайдено
     * @throws DataAccessException при помилках доступу до бази даних
     */
    @Transactional
    public void deleteChat(Long id) {
        if (id == null) {
            log.error("Спроба видалення чату з null ідентифікатором");
            throw new IllegalArgumentException("Ідентифікатор чату не може бути null");
        }

        if (id <= 0) {
            log.error("Спроба видалення чату з некоректним ідентифікатором: {}", id);
            throw new IllegalArgumentException("Ідентифікатор чату повинен бути додатним числом");
        }

        log.info("Спроба видалення чату з ID: {}", id);

        try {
            if (!chatRepository.existsById(id)) {
                log.error("Помилка видалення: чат з ID {} не знайдено", id);
                throw new EntityNotFoundException("Чат з ідентифікатором " + id + " не знайдено");
            }

            chatRepository.deleteById(id);
            log.info("Чат з ID: {} успішно видалено", id);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (DataAccessException e) {
            log.error("Помилка доступу до бази даних при видаленні чату з ID {}: {}", id, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Непередбачена помилка при видаленні чату з ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Помилка видалення чату", e);
        }
    }
}