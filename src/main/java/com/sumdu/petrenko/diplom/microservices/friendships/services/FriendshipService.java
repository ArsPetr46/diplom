package com.sumdu.petrenko.diplom.microservices.friendships.services;

import com.sumdu.petrenko.diplom.clients.ChatServiceClient;
import com.sumdu.petrenko.diplom.clients.UserServiceClient;
import com.sumdu.petrenko.diplom.dto.FriendshipDTO;
import com.sumdu.petrenko.diplom.dto.UserDTO;
import com.sumdu.petrenko.diplom.mappers.FriendshipMapper;
import com.sumdu.petrenko.diplom.microservices.friendships.models.FriendshipEntity;
import com.sumdu.petrenko.diplom.microservices.friendships.models.FriendshipId;
import com.sumdu.petrenko.diplom.microservices.friendships.repositories.FriendshipRepository;
import com.sumdu.petrenko.diplom.microservices.messages.services.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Сервіс для роботи з дружбами.
 * <p>
 * Цей клас надає методи для обробки дружб, включаючи їх створення, видалення, оновлення та отримання.
 * Реалізує бізнес-логіку для управління сутностями дружб та їх DTO-представленнями.
 * </p>
 * <p>
 * Сервіс взаємодіє з репозиторієм для виконання операцій з базою даних та використовує маппер
 * для конвертації між сутностями та DTO. Методи, що модифікують дані, позначені анотацією
 * {@code @Transactional} для забезпечення атомарності операцій.
 * </p>
 * <p>
 * Сервіс також взаємодіє з іншими мікросервісами через відповідні клієнти (UserServiceClient, ChatServiceClient)
 * для забезпечення узгодженості даних між різними частинами системи.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FriendshipService {
    /**
     * Репозиторій для роботи з дружбами.
     * <p>
     * Забезпечує доступ до даних дружб у базі даних через JPA інтерфейс.
     * Надає методи для виконання базових CRUD-операцій, а також спеціалізовані методи пошуку.
     * </p>
     */
    private final FriendshipRepository friendshipRepository;

    /**
     * Клієнт для взаємодії з мікросервісом користувачів.
     * <p>
     * Використовується для перевірки існування користувачів перед створенням дружби
     * та для отримання додаткової інформації про користувачів.
     * </p>
     */
    private final UserServiceClient userServiceClient;

    /**
     * Клієнт для взаємодії з мікросервісом чатів.
     * <p>
     * Використовується для перевірки існування чатів перед створенням дружби
     * та для координації даних між дружбами та чатами.
     * </p>
     */
    private final ChatServiceClient chatServiceClient;

    /**
     * Маппер для конвертації між сутностями дружби та DTO.
     * <p>
     * Відповідає за перетворення внутрішніх сутностей {@code FriendshipEntity} на об'єкти передачі даних
     * {@code FriendshipDTO}, які надаються зовнішнім клієнтам. Це дозволяє розділити внутрішню модель
     * даних від представлення, що експортується через API.
     * </p>
     */
    private final FriendshipMapper friendshipMapper;

    /**
     * Отримати дружбу за id двох користувачів.
     * <p>
     * Метод шукає запис про дружбу в базі даних за ідентифікаторами двох користувачів.
     * Використовується композитний ключ FriendshipId для пошуку запису.
     * </p>
     *
     * @param userId1 ід першого користувача
     * @param userId2 ід другого користувача
     * @return Optional з сутністю дружби або порожній Optional, якщо дружбу не знайдено
     * @throws DataAccessException у разі проблем з доступом до бази даних
     */
    public Optional<FriendshipEntity> getFriendshipById(Long userId1, Long userId2) {
        try {
            log.info("Отримання дружби між користувачами з id {} та {}", userId1, userId2);
            return friendshipRepository.findById(new FriendshipId(userId1, userId2));
        } catch (DataAccessException e) {
            log.error("Помилка бази даних при отриманні дружби між користувачами {} та {}: {}", userId1, userId2, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Непередбачена помилка при отриманні дружби між користувачами {} та {}: {}", userId1, userId2, e.getMessage());
            throw e;
        }
    }

    /**
     * Отримати всіх друзів користувача.
     * <p>
     * Метод знаходить всі записи про дружбу, де вказаний користувач є одним із учасників,
     * і перетворює їх на DTO з точки зору цього користувача.
     * </p>
     *
     * @param userId ID користувача
     * @return список DTO з інформацією про дружбу (може бути порожнім)
     * @throws DataAccessException у разі проблем з доступом до бази даних
     */
    public List<FriendshipDTO> getFriendsOfUser(Long userId) {
        try {
            log.info("Отримання списку друзів для користувача з id {}", userId);

            if (userId == null || userId <= 0) {
                log.warn("Спроба отримати друзів з некоректним ID користувача: {}", userId);
                return Collections.emptyList();
            }

            List<FriendshipDTO> friends = friendshipRepository.findByUserId(userId)
                    .stream()
                    .map(entity -> friendshipMapper.toDto(entity, userId))
                    .toList();

            log.info("Знайдено {} друзів для користувача з id {}", friends.size(), userId);
            return friends;
        } catch (DataAccessException e) {
            log.error("Помилка бази даних при отриманні друзів користувача {}: {}", userId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Непередбачена помилка при отриманні друзів користувача {}: {}", userId, e.getMessage());
            throw e;
        }
    }

    /**
     * Перевірити, чи існує дружба між двома користувачами.
     * <p>
     * Метод перевіряє наявність запису про дружбу між вказаними користувачами в базі даних.
     * Використовується для швидкої перевірки статусу відносин без необхідності отримувати всі дані.
     * </p>
     *
     * @param userId1 ід першого користувача
     * @param userId2 ід другого користувача
     * @return true, якщо дружба існує, false - якщо не існує
     * @throws DataAccessException у разі проблем з доступом до бази даних
     */
    public boolean existsFriendship(Long userId1, Long userId2) {
        try {
            log.info("Перевірка існування дружби між користувачами з id {} та {}", userId1, userId2);

            if (userId1 == null || userId1 <= 0 || userId2 == null || userId2 <= 0) {
                log.warn("Спроба перевірити дружбу з некоректними ID користувачів: {} та {}", userId1, userId2);
                return false;
            }

            boolean exists = friendshipRepository.existsById(new FriendshipId(userId1, userId2));
            log.info("Дружба між користувачами з id {} та {} {}", userId1, userId2, exists ? "існує" : "не існує");
            return exists;
        } catch (DataAccessException e) {
            log.error("Помилка бази даних при перевірці існування дружби між користувачами {} та {}: {}",
                    userId1, userId2, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Непередбачена помилка при перевірці існування дружби між користувачами {} та {}: {}",
                    userId1, userId2, e.getMessage());
            throw e;
        }
    }

    /**
     * Зберегти дружбу.
     * <p>
     * Метод створює новий запис про дружбу між двома користувачами. Перед збереженням
     * виконується перевірка існування обох користувачів та вказаного чату за допомогою
     * відповідних мікросервісів.
     * </p>
     * <p>
     * Метод виконується в межах транзакції для забезпечення атомарності операції.
     * </p>
     *
     * @param friendshipEntity дружба для збереження
     * @return збережена дружба з встановленими ідентифікаторами
     * @throws IllegalArgumentException якщо користувачі або чат не існують
     * @throws DataIntegrityViolationException якщо порушуються обмеження цілісності даних
     * @throws DataAccessException у разі інших проблем з доступом до бази даних
     */
    @Transactional
    public FriendshipEntity saveFriendship(FriendshipEntity friendshipEntity) {
        try {
            log.info("Збереження дружби між користувачами {} та {}",
                    friendshipEntity.getUserId1(), friendshipEntity.getUserId2());

            if (friendshipEntity == null) {
                log.error("Спроба зберегти null-об'єкт дружби");
                throw new IllegalArgumentException("Об'єкт дружби не може бути null");
            }

            if (friendshipEntity.getUserId1() == null || friendshipEntity.getUserId1() <= 0) {
                log.error("Спроба зберегти дружбу з некоректним ID першого користувача: {}",
                        friendshipEntity.getUserId1());
                throw new IllegalArgumentException("ID першого користувача має бути додатним числом");
            }

            if (friendshipEntity.getUserId2() == null || friendshipEntity.getUserId2() <= 0) {
                log.error("Спроба зберегти дружбу з некоректним ID другого користувача: {}",
                        friendshipEntity.getUserId2());
                throw new IllegalArgumentException("ID другого користувача має бути додатним числом");
            }

            if (friendshipEntity.getChatId() == null || friendshipEntity.getChatId() <= 0) {
                log.error("Спроба зберегти дружбу з некоректним ID чату: {}",
                        friendshipEntity.getChatId());
                throw new IllegalArgumentException("ID чату має бути додатним числом");
            }

            ResponseEntity<Boolean> user1Exists = userServiceClient.existsById(friendshipEntity.getUserId1());
            if (!user1Exists.getStatusCode().is2xxSuccessful() || !Boolean.TRUE.equals(user1Exists.getBody())) {
                log.error("Не вдалось створити дружбу: користувач з ID {} не існує", friendshipEntity.getUserId1());
                throw new IllegalArgumentException("Користувач з ID " + friendshipEntity.getUserId1() + " не існує");
            }

            ResponseEntity<Boolean> user2Exists = userServiceClient.existsById(friendshipEntity.getUserId2());
            if (!user2Exists.getStatusCode().is2xxSuccessful() || !Boolean.TRUE.equals(user2Exists.getBody())) {
                log.error("Не вдалось створити дружбу: користувач з ID {} не існує", friendshipEntity.getUserId2());
                throw new IllegalArgumentException("Користувач з ID " + friendshipEntity.getUserId2() + " не існує");
            }

            ResponseEntity<Boolean> chatExists = chatServiceClient.existsById(friendshipEntity.getChatId());
            if (!chatExists.getStatusCode().is2xxSuccessful() || !Boolean.TRUE.equals(chatExists.getBody())) {
                log.error("Не вдалось створити дружбу: чат з ID {} не існує", friendshipEntity.getChatId());
                throw new IllegalArgumentException("Чат з ID " + friendshipEntity.getChatId() + " не існує");
            }

            FriendshipEntity savedFriendship = friendshipRepository.save(friendshipEntity);
            log.info("Дружба між користувачами {} та {} успішно збережена",
                    savedFriendship.getUserId1(), savedFriendship.getUserId2());
            return savedFriendship;
        } catch (DataIntegrityViolationException e) {
            log.error("Помилка цілісності даних при збереженні дружби: {}", e.getMessage());
            throw e;
        } catch (DataAccessException e) {
            log.error("Помилка бази даних при збереженні дружби: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Непередбачена помилка при збереженні дружби: {}", e.getMessage());
            throw e;
        }
    }


    /**
     * Отримати кількість спільних друзів для двох користувачів.
     * <p>
     * Метод знаходить спільних друзів між двома користувачами, аналізуючи їх списки друзів
     * та розраховуючи перетин цих списків.
     * </p>
     *
     * @param userId1 ід першого користувача
     * @param userId2 ід другого користувача
     * @return кількість спільних друзів (може бути 0)
     * @throws DataAccessException у разі проблем з доступом до бази даних
     */
    public Integer getMutualFriendsCount(Long userId1, Long userId2) {
        try {
            log.info("Отримання кількості спільних друзів між користувачами з id {} та {}", userId1, userId2);

            if (userId1 == null || userId1 <= 0 || userId2 == null || userId2 <= 0) {
                log.warn("Спроба отримати кількість спільних друзів з некоректними ID користувачів: {} та {}", userId1, userId2);
                return 0;
            }

            List<Long> friends1 = friendshipRepository.findByUserId1OrUserId2(userId1)
                    .stream()
                    .map(friendship -> friendshipMapper.getFriendId(friendship, userId1))
                    .toList();

            List<Long> friends2 = friendshipRepository.findByUserId1OrUserId2(userId2)
                    .stream()
                    .map(friendship -> friendshipMapper.getFriendId(friendship, userId2))
                    .toList();

            int mutualCount = (int) friends1.stream()
                    .filter(friends2::contains)
                    .count();

            log.info("Кількість спільних друзів між користувачами з id {} та {} становить {}", userId1, userId2, mutualCount);
            return mutualCount;
        } catch (DataAccessException e) {
            log.error("Помилка бази даних при отриманні кількості спільних друзів між користувачами {} та {}: {}",
                    userId1, userId2, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Непередбачена помилка при отриманні кількості спільних друзів між користувачами {} та {}: {}",
                    userId1, userId2, e.getMessage());
            throw e;
        }
    }

    /**
     * Змінити статус блокування дружби для конкретного користувача.
     * <p>
     * Метод перемикає статус блокування дружби з боку вказаного користувача.
     * Спочатку перевіряється, чи є користувач учасником дружби, потім змінюється
     * відповідне поле блокування (blockByUser1 або blockByUser2) залежно від ролі користувача.
     * </p>
     * <p>
     * Метод виконується в межах транзакції для забезпечення атомарності операції.
     * </p>
     *
     * @param userId1 ід першого користувача (дружби)
     * @param userId2 ід другого користувача (який блокує)
     * @return Optional з оновленим DTO дружби або порожній Optional, якщо дружбу не знайдено
     * @throws IllegalArgumentException якщо користувач не є учасником дружби
     * @throws DataAccessException у разі проблем з доступом до бази даних
     */
    @Transactional
    public Optional<FriendshipDTO> toggleBlockStatus(Long userId1, Long userId2) {
        try {
            log.info("Зміна статусу блокування для дружби між користувачами {} та {}", userId1, userId2);

            if (userId1 == null || userId1 <= 0 || userId2 == null || userId2 <= 0) {
                log.warn("Спроба змінити статус блокування з некоректними ID користувачів: {} та {}", userId1, userId2);
                throw new IllegalArgumentException("ID користувачів мають бути додатними числами");
            }

            return friendshipRepository.findById(new FriendshipId(userId1, userId2))
                    .map(friendship -> {
                        boolean isCurrentUserFirst = friendship.getId().getUserId1().equals(userId1);

                        if (!friendship.getUserId1().equals(userId1) && !friendship.getUserId2().equals(userId2)) {
                            log.warn("Спроба змінити статус блокування користувачем, який не є учасником дружби");
                            throw new IllegalArgumentException("Користувач не є учасником цієї дружби");
                        }

                        if (isCurrentUserFirst) {
                            friendship.setBlockedByUser1(!friendship.isBlockedByUser1());
                            log.info("Статус блокування користувачем {} для дружби змінено на {}",
                                    userId1, friendship.isBlockedByUser1());
                        } else {
                            friendship.setBlockedByUser2(!friendship.isBlockedByUser2());
                            log.info("Статус блокування користувачем {} для дружби змінено на {}",
                                    userId2, friendship.isBlockedByUser2());
                        }

                        friendship = friendshipRepository.save(friendship);
                        FriendshipDTO dto = friendshipMapper.toDto(friendship, userId1);
                        log.info("Зміна статусу блокування для дружби між користувачами {} та {} успішно завершена", userId1, userId2);
                        return dto;
                    });
        } catch (DataAccessException e) {
            log.error("Помилка бази даних при зміні статусу блокування дружби між користувачами {} та {}: {}",
                    userId1, userId2, e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Непередбачена помилка при зміні статусу блокування дружби між користувачами {} та {}: {}",
                    userId1, userId2, e.getMessage());
            throw e;
        }
    }
}
