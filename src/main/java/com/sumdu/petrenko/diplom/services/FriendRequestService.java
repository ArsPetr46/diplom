package com.sumdu.petrenko.diplom.services;

import com.sumdu.petrenko.diplom.models.FriendRequest;
import com.sumdu.petrenko.diplom.models.Friendship;
import com.sumdu.petrenko.diplom.repositories.FriendRequestRepository;
import com.sumdu.petrenko.diplom.repositories.FriendshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Сервіс для роботи з запитами на дружбу.
 * <p>
 * Цей клас надає методи для обробки запитів на дружбу, включаючи їх створення, прийняття та скасування.
 * </p>
 */
@Service
public class FriendRequestService {
    /**
     * Логер для сервісу запитів на дружбу.
     */
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(FriendRequestService.class);

    /**
     * Репозиторій для роботи з запитами на дружбу.
     */
    private final FriendRequestRepository friendRequestRepository;
    /**
     * Репозиторій для роботи з дружбами.
     */
    private final FriendshipRepository friendshipRepository;

    /**
     * Конструктор сервісу запитів на дружбу.
     *
     * @param friendRequestRepository репозиторій для роботи з запитами на дружбу
     * @param friendshipRepository    репозиторій для роботи з дружбами
     */
    @Autowired
    public FriendRequestService(FriendRequestRepository friendRequestRepository, FriendshipRepository friendshipRepository) {
        this.friendRequestRepository = friendRequestRepository;
        this.friendshipRepository = friendshipRepository;
    }

    /**
     * Отримати запит на дружбу за його id.
     *
     * @param id ід запиту на дружбу
     * @return запит на дружбу
     */
    public Optional<FriendRequest> getFriendRequestById(Long id) {
        logger.info("Отримання запиту на дружбу з id {}", id);
        return friendRequestRepository.findById(id);
    }

    /**
     * Отримати всі запити на дружбу, відправлені користувачем.
     *
     * @param senderId ід відправника
     * @return список запитів на дружбу
     */
    public List<FriendRequest> getFriendRequestsBySenderId(Long senderId) {
        logger.info("Отримання всіх запитів на дружбу, відправлених користувачем з id {}", senderId);
        return friendRequestRepository.findBySenderId(senderId);
    }

    /**
     * Отримати всі запити на дружбу, отримані користувачем.
     *
     * @param receiverId ід отримувача
     * @return список запитів на дружбу
     */
    public List<FriendRequest> getFriendRequestsByReceiverId(Long receiverId) {
        logger.info("Отримання всіх запитів на дружбу, отриманих користувачем з id {}", receiverId);
        return friendRequestRepository.findByReceiverId(receiverId);
    }

    /**
     * Зберегти запит на дружбу.
     *
     * @param friendRequest запит на дружбу
     * @return збережений запит на дружбу
     */
    public FriendRequest saveFriendRequest(FriendRequest friendRequest) {
        logger.info("Збереження запиту на дружбу з id {} від користувача з id {}", friendRequest.getId(), friendRequest.getSender().getId());
        return friendRequestRepository.save(friendRequest);
    }

    /**
     * Видалити запит на дружбу за його id.
     *
     * @param id ід запиту на дружбу
     */
    public void deleteFriendRequest(Long id) {
        logger.info("Видалення запиту на дружбу з id {}", id);
        friendRequestRepository.deleteById(id);
    }

    /**
     * Прийняти запит на дружбу.
     *
     * @param requestId ід запиту на дружбу
     */
    public void acceptFriendRequest(Long requestId) {
        Optional<FriendRequest> friendRequestOpt = friendRequestRepository.findById(requestId);

        if (friendRequestOpt.isPresent()) {
            FriendRequest friendRequest = friendRequestOpt.get();

            Friendship friendship = new Friendship(
                    friendRequest.getSender(),
                    friendRequest.getReceiver()
            );

            friendshipRepository.save(friendship);
            friendRequestRepository.deleteById(requestId);
            logger.info("Запит на дружбу з id {} успішно прийнято", requestId);
        } else {
            logger.warn("Неможливо ть прийняти запит на дружбу: запит не знайдено з id {}", requestId);
            throw new IllegalArgumentException("Friend request not found");
        }
    }

    /**
     * Відхилити запит на дружбу.
     *
     * @param requestId ід запиту на дружбу
     */
    public void cancelFriendRequest(Long requestId) {
        if (friendRequestRepository.existsById(requestId)) {
            friendRequestRepository.deleteById(requestId);
            logger.info("Запит на дружбу з id {} успішно відхилено", requestId);
        } else {
            logger.warn("Неможливо відхилити запит на дружбу: запит не знайдено з id {}", requestId);
            throw new IllegalArgumentException("Friend request not found");
        }
    }

    /**
     * Отримати статус дружби між двома користувачами.
     *
     * @param userId1 ід першого користувача
     * @param userId2 ід другого користувача
     * @return статус дружби
     */
    public String getFriendshipStatus(Long userId1, Long userId2) {
        Optional<FriendRequest> sentRequest = friendRequestRepository.findBySenderIdAndReceiverId(userId1, userId2);
        Optional<FriendRequest> receivedRequest = friendRequestRepository.findBySenderIdAndReceiverId(userId2, userId1);

        if (sentRequest.isPresent()) {
            logger.info("Запит на дружбу з id {} відправлено користувачу з id {}", userId1, userId2);
            return "Pending";
        } else if (receivedRequest.isPresent()) {
            logger.info("Запит на дружбу з id {} отримано від користувача з id {}", userId2, userId1);
            return "Received";
        } else {
            Optional<Friendship> friendship = friendshipRepository.findByUserIdAndFriendId(userId1, userId2);
            if (friendship.isPresent()) {
                logger.info("Користувачі з id {} та id {} є друзями", userId1, userId2);
                return "Friends";
            } else {
                logger.info("Користувачі з id {} та id {} не є друзями", userId1, userId2);
                return "Not Friends";
            }
        }
    }
}