package com.sumdu.petrenko.diplom.microservices.users.services;

import com.sumdu.petrenko.diplom.dto.UserDTO;
import com.sumdu.petrenko.diplom.mappers.UserMapper;
import com.sumdu.petrenko.diplom.microservices.users.models.UserEntity;
import com.sumdu.petrenko.diplom.microservices.users.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Сервіс для роботи з користувачами.
 * <p>
 * Цей клас надає методи для обробки користувачів, включаючи їх створення, видалення, оновлення та отримання.
 * Реалізує бізнес-логіку для управління сутностями користувачів та їх DTO-представленнями.
 * </p>
 * <p>
 * Сервіс взаємодіє з репозиторієм для виконання операцій з базою даних та використовує маппер
 * для конвертації між сутностями та DTO. Методи, що модифікують дані, позначені анотацією
 * {@code @Transactional} для забезпечення атомарності операцій.
 * </p>
 * <p>
 * Реалізує паттерн "Фасад" для надання спрощеного інтерфейсу до підсистеми управління користувачами.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    /**
     * Репозиторій для роботи з користувачами.
     * <p>
     * Забезпечує доступ до даних користувачів у базі даних через JPA інтерфейс.
     * Надає методи для виконання базових CRUD-операцій, а також спеціалізовані методи пошуку.
     * </p>
     */
    private final UserRepository userRepository;

    /**
     * Маппер для конвертації між сутностями та DTO користувачів.
     * <p>
     * Відповідає за перетворення внутрішніх сутностей {@code UserEntity} на об'єкти передачі даних
     * {@code UserDTO}, які надаються зовнішнім клієнтам. Це дозволяє розділити внутрішню модель
     * даних від представлення, що експортується через API.
     * </p>
     */
    private final UserMapper userMapper;

    /**
     * Кодувальник паролів для шифрування паролів користувачів.
     * <p>
     * Використовується для безпечного зберігання паролів у базі даних. Забезпечує
     * функцію хешування паролів перед їх збереженням, а також перевірку паролів
     * під час аутентифікації користувачів.
     * </p>
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Отримати користувача за його ідентифікатором.
     * <p>
     * Метод шукає користувача в базі даних за вказаним id та конвертує його в DTO.
     * Очікується, що валідація id вже виконана на рівні контролера.
     * </p>
     *
     * @param id ідентифікатор користувача
     * @return Optional з DTO користувача або порожній Optional, якщо користувача не знайдено
     */
    public Optional<UserDTO> getUserById(Long id) {
        try {
            Optional<UserDTO> result = userRepository.findById(id)
                    .map(userMapper::toUserDTO);

            if (result.isPresent()) {
                log.info("Знайдено користувача з id {}", id);
            } else {
                log.info("Користувача з id {} не знайдено", id);
            }

            return result;
        } catch (DataAccessException e) {
            log.error("Помилка доступу до БД при отриманні користувача з id {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Непередбачена помилка при отриманні користувача з id {}: {}", id, e.getMessage());
            throw e;
        }
    }

    /**
     * Перевірити, чи існує користувач за його ідентифікатором.
     * <p>
     * Метод використовується для швидкої перевірки існування користувача без необхідності
     * отримувати всі його дані. Може використовуватися іншими мікросервісами для валідації
     * посилань на користувачів.
     * </p>
     *
     * @param id ідентифікатор користувача
     * @return true, якщо користувач існує, false - якщо ні
     */
    public boolean existsById(Long id) {
        try {
            boolean exists = userRepository.existsById(id);

            if (exists) {
                log.info("Користувача з id {} знайдено", id);
            }
            else {
                log.info("Користувача з id {} не знайдено", id);
            }

            return exists;
        } catch (DataAccessException e) {
            log.error("Помилка доступу до БД при перевірці існування користувача з id {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Непередбачена помилка при перевірці існування користувача з id {}: {}", id, e.getMessage());
            throw e;
        }
    }

    /**
     * Перевірити, чи існує користувач за його email.
     * <p>
     * Метод перевіряє існування користувача з вказаною електронною поштою.
     * Використовується для валідації унікальності email при реєстрації нових користувачів.
     * </p>
     *
     * @param email електронна пошта користувача
     * @return true, якщо користувач з такою поштою існує, false - якщо ні
     */
    public boolean existsByEmail(String email) {
        try {
            boolean exists = userRepository.existsByEmail(email);

            if (exists) {
                log.info("Знайдено користувача з email {}", email);
            }
            else {
                log.info("Користувача з email {} не знайдено", email);
            }

            return exists;
        } catch (DataAccessException e) {
            log.error("Помилка доступу до БД при перевірці існування користувача з email {}: {}", email, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Непередбачена помилка при перевірці існування користувача з email {}: {}", email, e.getMessage());
            throw e;
        }
    }

    /**
     * Перевірити, чи існує користувач за його нікнеймом.
     * <p>
     * Метод перевіряє існування користувача з вказаним нікнеймом.
     * Використовується для валідації унікальності нікнейму при реєстрації або оновленні даних користувачів.
     * </p>
     *
     * @param nickname нікнейм користувача
     * @return true, якщо користувач з таким нікнеймом існує, false - якщо ні
     */
    public boolean existsByNickname(String nickname) {
        try {
            boolean exists = userRepository.existsByNickname(nickname);

            if (exists) {
                log.info("Знайдено користувача з нікнеймом {}", nickname);
            }
            else {
                log.info("Користувача з нікнеймом {} не знайдено", nickname);
            }

            return exists;
        } catch (DataAccessException e) {
            log.error("Помилка доступу до БД при перевірці існування користувача з нікнеймом {}: {}", nickname, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Непередбачена помилка при перевірці існування користувача з нікнеймом {}: {}", nickname, e.getMessage());
            throw e;
        }
    }

    /**
     * Отримати користувачів, нікнейм яких містить вказаний підрядок.
     * <p>
     * Метод виконує пошук користувачів, чий нікнейм містить вказаний підрядок без урахування регістру.
     * Використовується для функціоналу пошуку користувачів за частиною нікнейму.
     * </p>
     *
     * @param nickname підрядок для пошуку в нікнеймах користувачів
     * @return список DTO користувачів, чиї нікнейми містять вказаний підрядок (може бути порожнім)
     */
    public List<UserDTO> getUsersByNicknameContaining(String nickname) {
        try {
            List<UserEntity> users = userRepository.findByNicknameContainingIgnoreCase(nickname);
            List<UserDTO> userDTOs = users.stream()
                    .map(userMapper::toUserDTO)
                    .toList();

            log.info("Знайдено {} користувачів з нікнеймом, що містить '{}'", userDTOs.size(), nickname);
            return userDTOs;
        } catch (DataAccessException e) {
            log.error("Помилка доступу до БД при пошуку користувачів за нікнеймом '{}': {}", nickname, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Непередбачена помилка при пошуку користувачів за нікнеймом '{}': {}", nickname, e.getMessage());
            throw e;
        }
    }

    /**
     * Отримати користувачів за списком їх ідентифікаторів.
     * <p>
     * Метод дозволяє отримати дані кількох користувачів за один запит за їх ідентифікаторами.
     * Це оптимізує взаємодію з базою даних при необхідності отримати дані багатьох користувачів.
     * </p>
     *
     * @param userIds список ідентифікаторів користувачів
     * @return список DTO знайдених користувачів (може бути порожнім)
     */
    public List<UserDTO> getUsersByIds(List<Long> userIds) {
        if (userIds.isEmpty()) {
            return Collections.emptyList();
        }

        try {
            List<UserEntity> users = userRepository.findAllById(userIds);
            List<UserDTO> userDTOs = users.stream()
                    .map(userMapper::toUserDTO)
                    .toList();

            log.info("Знайдено {} користувачів з {} запитаних id", userDTOs.size(), userIds.size());
            return userDTOs;
        } catch (DataAccessException e) {
            log.error("Помилка доступу до БД при отриманні користувачів за списком ID: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Непередбачена помилка при отриманні користувачів за списком ID: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Оновити існуючого користувача.
     * <p>
     * Метод оновлює дані існуючого користувача. Перевіряє наявність користувача
     * з вказаним ID та оновлює його поля відповідно до наданих даних.
     * </p>
     * <p>
     * Метод виконується в межах транзакції для забезпечення атомарності операції.
     * При оновленні перевіряється наявність кожного поля у вхідних даних, щоб
     * не перезаписувати існуючі дані null-значеннями.
     * </p>
     *
     * @param id ідентифікатор користувача, якого потрібно оновити
     * @param userDetails нові дані користувача
     * @return Optional з DTO оновленого користувача або порожній Optional, якщо користувача не знайдено
     */
    @Transactional
    public Optional<UserDTO> updateUser(Long id, UserEntity userDetails) {
        try {
            Optional<UserEntity> existingOptional = userRepository.findById(id);

            if (existingOptional.isEmpty()) {
                log.info("Не знайдено користувача з id {} для оновлення", id);
                return Optional.empty();
            }

            UserEntity existing = existingOptional.get();

            if (userDetails.getNickname() != null) {
                existing.setNickname(userDetails.getNickname());
            }

            if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                if (!userDetails.getPassword().equals(existing.getPassword())) {
                    existing.setPassword(passwordEncoder.encode(userDetails.getPassword()));
                }
            }

            existing.setUserDescription(userDetails.getUserDescription());
            existing.setBirthDate(userDetails.getBirthDate());
            existing.setAvatarUrl(userDetails.getAvatarUrl());

            UserEntity saved = userRepository.save(existing);
            UserDTO userDTO = userMapper.toUserDTO(saved);

            log.info("Оновлено дані користувача з id {}", id);
            return Optional.of(userDTO);
        } catch (DataAccessException e) {
            log.error("Помилка доступу до БД при оновленні користувача з id {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Непередбачена помилка при оновленні користувача з id {}: {}", id, e.getMessage());
            throw e;
        }
    }


    /**
     * Видалити користувача за його ідентифікатором.
     * <p>
     * Метод видаляє користувача з системи за його ідентифікатором. Операція незворотна.
     * Перед видаленням перевіряється існування користувача з вказаним ID.
     * </p>
     * <p>
     * Метод виконується в межах транзакції для забезпечення атомарності операції.
     * Якщо з користувачем пов'язані інші дані, може виникнути помилка обмеження цілісності,
     * яка буде перехоплена та передана викликаючому коду.
     * </p>
     *
     * @param id ідентифікатор користувача, якого потрібно видалити
     * @return true, якщо користувача видалено, false - якщо його не знайдено
     */
    @Transactional
    public boolean deleteUser(Long id) {
        try {
            return userRepository.findById(id)
                    .map(user -> {
                        userRepository.delete(user);
                        log.info("Користувач з id {} успішно видалений", id);
                        return true;
                    })
                    .orElseGet(() -> {
                        log.info("Спроба видалити неіснуючого користувача з id {}", id);
                        return false;
                    });
        } catch (DataAccessException e) {
            log.error("Помилка доступу до БД при видаленні користувача з id {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Непередбачена помилка при видаленні користувача з id {}: {}", id, e.getMessage());
            throw e;
        }
    }
}
