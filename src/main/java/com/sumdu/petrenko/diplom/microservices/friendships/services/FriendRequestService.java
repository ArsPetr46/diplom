package com.sumdu.petrenko.diplom.microservices.friendships.services;

import com.sumdu.petrenko.diplom.clients.ChatServiceClient;
import com.sumdu.petrenko.diplom.clients.UserServiceClient;
import com.sumdu.petrenko.diplom.microservices.friendships.models.FriendRequestEntity;
import com.sumdu.petrenko.diplom.microservices.friendships.models.FriendRequestId;
import com.sumdu.petrenko.diplom.microservices.friendships.models.FriendshipEntity;
import com.sumdu.petrenko.diplom.microservices.friendships.repositories.FriendRequestRepository;
import com.sumdu.petrenko.diplom.microservices.friendships.repositories.FriendshipRepository;
import com.sumdu.petrenko.diplom.microservices.messages.services.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Сервіс для управління запитами на дружбу.
 * <p>
 * Цей сервіс надає методи для створення, пошуку, прийняття та відхилення запитів на дружбу між
 * користувачами. Він взаємодіє з {@link FriendRequestRepository} для доступу до даних
 * та виконує бізнес-логіку, пов'язану з запитами на дружбу.
 * </p>
 * <p>
 * Цей сервіс також взаємодіє з іншими сервісами через клієнти:
 * <ul>
 *   <li>{@link UserServiceClient} - для перевірки існування користувачів</li>
 *   <li>{@link ChatServiceClient} - для створення чатів при прийнятті запитів на дружбу</li>
 *   <li>{@link FriendshipService} - для створення зв'язків дружби між користувачами</li>
 * </ul>
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FriendRequestService {
    /**
     * Репозиторій для роботи з запитами на дружбу.
     */
    private final FriendRequestRepository friendRequestRepository;

    /**
     * Репозиторій для роботи з дружбами.
     */
    private final FriendshipService friendshipService;

    /**
     * Сервіс для роботи з користувачами.
     */
    private final UserServiceClient userServiceClient;

    /**
     * Сервіс для роботи з чатами.
     */
    private final ChatServiceClient chatServiceClient;

    /**
     * Отримує запит на дружбу за ID відправника та отримувача.
     *
     * @param senderId ID відправника запиту
     * @param receiverId ID отримувача запиту
     * @return Optional з запитом на дружбу, якщо він існує
     * @throws IllegalArgumentException якщо будь-який з ID є null або якщо це той самий користувач
     */
    public Optional<FriendRequestEntity> getFriendRequestById(Long senderId, Long receiverId) {
        if (senderId == null || receiverId == null) {
            log.error("ID відправника або отримувача є null");
            throw new IllegalArgumentException("ID відправника та отримувача не можуть бути null");
        }

        if (Objects.equals(senderId, receiverId)) {
            log.error("ID відправника та отримувача однакові: {}", senderId);
            throw new IllegalArgumentException("Відправник та отримувач не можуть бути однією особою");
        }

        return friendRequestRepository.findById(new FriendRequestId(senderId, receiverId));
    }

    /**
     * Отримати всі запити на дружбу, відправлені користувачем.
     *
     * @param senderId ід відправника
     * @return список запитів на дружбу
     * @throws IllegalArgumentException якщо ID відправника є null
     */
    public List<FriendRequestEntity> getFriendRequestsBySenderId(Long senderId) {
        if (senderId == null) {
            log.error("ID відправника є null");
            throw new IllegalArgumentException("ID відправника не може бути null");
        }

        ResponseEntity<Boolean> senderExists = userServiceClient.existsById(senderId);
        if (isUserValid(senderExists)) {
            log.error("Відправник з ID {} не існує", senderId);
            throw new IllegalArgumentException("Відправник не існує");
        }

        return friendRequestRepository.findBySenderId(senderId);
    }

    /**
     * Отримати всі запити на дружбу, отримані користувачем.
     *
     * @param receiverId ід отримувача
     * @return список запитів на дружбу
     * @throws IllegalArgumentException якщо ID отримувача є null
     */
    public List<FriendRequestEntity> getFriendRequestsByReceiverId(Long receiverId) {
        if (receiverId == null) {
            log.error("ID отримувача є null");
            throw new IllegalArgumentException("ID отримувача не може бути null");
        }

        ResponseEntity<Boolean> receiverExists = userServiceClient.existsById(receiverId);
        if (isUserValid(receiverExists)) {
            log.error("Отримувач з ID {} не існує", receiverId);
            throw new IllegalArgumentException("Отримувач не існує");
        }

        return friendRequestRepository.findByReceiverId(receiverId);
    }

    /**
     * Перевірити, чи існує запит на дружбу між двома користувачами.
     *
     * @param senderId   ід відправника
     * @param receiverId ід отримувача
     * @return true, якщо запит на дружбу існує, false - якщо ні
     * @throws IllegalArgumentException якщо будь-який з ID є null або якщо це той самий користувач
     */
    public boolean existsFriendRequest(Long senderId, Long receiverId) {
        if (senderId == null || receiverId == null) {
            log.error("ID відправника або отримувача є null");
            throw new IllegalArgumentException("ID відправника та отримувача не можуть бути null");
        }

        if (Objects.equals(senderId, receiverId)) {
            log.error("ID відправника та отримувача однакові: {}", senderId);
            throw new IllegalArgumentException("Відправник та отримувач не можуть бути однією особою");
        }

        return friendRequestRepository.existsById(new FriendRequestId(senderId, receiverId));
    }

    /**
     * Зберегти запит на дружбу.
     *
     * @param friendRequestEntity запит на дружбу
     * @return збережений запит на дружбу
     * @throws IllegalArgumentException якщо запит є null, якщо відправник або отримувач не існують,
     *                                  якщо відправник та отримувач є однією особою, або якщо запит вже існує
     */
    @Transactional
    public FriendRequestEntity saveFriendRequest(FriendRequestEntity friendRequestEntity) {
        if (friendRequestEntity == null) {
            log.error("Запит на дружбу є null");
            throw new IllegalArgumentException("Запит на дружбу не може бути null");
        }

        if (friendRequestEntity.getId() == null) {
            log.error("ID запиту на дружбу є null");
            throw new IllegalArgumentException("ID запиту на дружбу не може бути null");
        }

        Long senderId = friendRequestEntity.getId().getSenderId();
        Long receiverId = friendRequestEntity.getId().getReceiverId();

        if (senderId == null || receiverId == null) {
            log.error("ID відправника або отримувача є null");
            throw new IllegalArgumentException("ID відправника та отримувача не можуть бути null");
        }

        if (Objects.equals(senderId, receiverId)) {
            log.error("ID відправника та отримувача однакові: {}", senderId);
            throw new IllegalArgumentException("Відправник та отримувач не можуть бути однією особою");
        }

        ResponseEntity<Boolean> senderExists = userServiceClient.existsById(senderId);
        ResponseEntity<Boolean> receiverExists = userServiceClient.existsById(receiverId);

        if (isUserValid(senderExists)) {
            log.error("Відправник з ID {} не існує", senderId);
            throw new IllegalArgumentException("Відправник не існує");
        }

        if (isUserValid(receiverExists)) {
            log.error("Отримувач з ID {} не існує", receiverId);
            throw new IllegalArgumentException("Отримувач не існує");
        }

        if (existsFriendRequest(senderId, receiverId)) {
            log.error("Запит на дружбу між користувачами {} та {} вже існує", senderId, receiverId);
            throw new IllegalArgumentException("Запит на дружбу вже існує");
        }

        log.info("Збереження запиту на дружбу від користувача з id {} до користувача з id {}", senderId, receiverId);
        return friendRequestRepository.save(friendRequestEntity);
    }

    /**
     * Видалити запит на дружбу.
     *
     * @param friendRequestEntity запит на дружбу
     * @return true, якщо запит на дружбу успішно видалено, false - якщо сталася помилка
     * @throws IllegalArgumentException якщо запит є null
     */
    @Transactional
    public boolean deleteFriendRequest(FriendRequestEntity friendRequestEntity) {
        if (friendRequestEntity == null) {
            log.error("Запит на дружбу є null");
            throw new IllegalArgumentException("Запит на дружбу не може бути null");
        }

        try {
            friendRequestRepository.delete(friendRequestEntity);
            log.info("Запит на дружбу з id {} успішно видалено", friendRequestEntity.getId());
            return true;
        } catch (Exception e) {
            log.error("Не вдалося видалити запит на дружбу з id {}: {}", friendRequestEntity.getId(), e.getMessage());
            return false;
        }
    }

    /**
     * Прийняти запит на дружбу.
     *
     * @param senderId ID відправника запиту
     * @param receiverId ID отримувача запиту
     * @throws IllegalArgumentException якщо будь-який з ID є null, якщо це той самий користувач,
     *                                  або якщо запит на дружбу не знайдено
     * @throws RuntimeException якщо виникла помилка створення чату або при видаленні запиту на дружбу
     */
    @Transactional
    public void acceptFriendRequest(Long senderId, Long receiverId) {
        if (senderId == null || receiverId == null) {
            log.error("ID відправника або отримувача є null");
            throw new IllegalArgumentException("ID відправника та отримувача не можуть бути null");
        }

        if (Objects.equals(senderId, receiverId)) {
            log.error("ID відправника та отримувача однакові: {}", senderId);
            throw new IllegalArgumentException("Відправник та отримувач не можуть бути однією особою");
        }

        FriendRequestId friendRequestId = new FriendRequestId(senderId, receiverId);

        friendRequestRepository.findById(friendRequestId)
                .map(request -> {
                    try {
                        ResponseEntity<Long> chatResponse = chatServiceClient.createChat();
                        if (!chatResponse.getStatusCode().is2xxSuccessful() || chatResponse.getBody() == null) {
                            throw new RuntimeException("Не вдалося створити чат");
                        }

                        FriendshipEntity friendship = new FriendshipEntity(
                                friendRequestId.getSenderId(),
                                friendRequestId.getReceiverId(),
                                chatResponse.getBody()
                        );

                        friendshipService.saveFriendship(friendship);

                        if (!deleteFriendRequest(request)) {
                            throw new RuntimeException("Не вдалося видалити запит на дружбу");
                        }

                        log.info("Запит на дружбу від користувача {} до користувача {} успішно прийнято", senderId, receiverId);
                        return request;
                    } catch (Exception e) {
                        log.error("Помилка при прийнятті запиту на дружбу: {}", e.getMessage());
                        throw new RuntimeException("Помилка при прийнятті запиту на дружбу: " + e.getMessage());
                    }
                })
                .orElseThrow(() -> {
                    log.warn("Неможливо прийняти запит на дружбу: запит від користувача {} до користувача {} не знайдено", senderId, receiverId);
                    return new IllegalArgumentException("Запит на дружбу не знайдено");
                });
    }

    /**
     * Відхилити запит на дружбу.
     *
     * @param senderId ID відправника запиту
     * @param receiverId ID отримувача запиту
     * @throws IllegalArgumentException якщо будь-який з ID є null, якщо це той самий користувач,
     *                                  або якщо запит на дружбу не знайдено
     * @throws RuntimeException якщо виникла помилка при видаленні запиту на дружбу
     */
    @Transactional
    public void rejectFriendRequest(Long senderId, Long receiverId) {
        if (senderId == null || receiverId == null) {
            log.error("ID відправника або отримувача є null");
            throw new IllegalArgumentException("ID відправника та отримувача не можуть бути null");
        }

        if (Objects.equals(senderId, receiverId)) {
            log.error("ID відправника та отримувача однакові: {}", senderId);
            throw new IllegalArgumentException("Відправник та отримувач не можуть бути однією особою");
        }

        friendRequestRepository.findById(new FriendRequestId(senderId, receiverId))
                .map(request -> {
                    if (!deleteFriendRequest(request)) {
                        throw new RuntimeException("Не вдалося видалити запит на дружбу");
                    }

                    log.info("Запит на дружбу від користувача {} до користувача {} успішно відхилено", senderId, receiverId);
                    return request;
                })
                .orElseThrow(() -> {
                    log.warn("Неможливо відхилити запит на дружбу: запит від користувача {} до користувача {} не знайдено", senderId, receiverId);
                    return new IllegalArgumentException("Запит на дружбу не знайдено");
                });
    }

    /**
     * Перевіряє, чи користувач існує на основі відповіді від сервісу користувачів.
     *
     * @param response відповідь від сервісу користувачів
     * @return true, якщо користувач не існує; false, якщо користувач існує
     */
    private boolean isUserValid(ResponseEntity<Boolean> response) {
        return !response.getStatusCode().is2xxSuccessful() ||
                !Boolean.TRUE.equals(response.getBody());
    }
}