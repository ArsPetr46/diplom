package com.sumdu.petrenko.diplom.services;

import com.sumdu.petrenko.diplom.dto.UserDTO;
import com.sumdu.petrenko.diplom.models.Friendship;
import com.sumdu.petrenko.diplom.models.User;
import com.sumdu.petrenko.diplom.repositories.FriendshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Сервіс для роботи з дружбами.
 * <p>
 * Цей клас надає методи для обробки дружб, включаючи їх створення, видалення та отримання списків друзів.
 * </p>
 */
@Service
public class FriendshipService {
    /**
     * Репозиторій для роботи з дружбами.
     */
    private final FriendshipRepository friendshipRepository;
    /**
     * Сервіс для роботи з користувачами.
     */
    private final UserService userService;

    /**
     * Конструктор сервісу дружб.
     *
     * @param friendshipRepository репозиторій для роботи з дружбами
     * @param userService          сервіс для роботи з користувачами
     */
    @Autowired
    public FriendshipService(FriendshipRepository friendshipRepository, UserService userService) {
        this.friendshipRepository = friendshipRepository;
        this.userService = userService;
    }

    /**
     * Отримати дружбу за її id.
     *
     * @param id ід дружби
     * @return дружба
     */
    public Optional<Friendship> getFriendshipById(Long id) {
        return friendshipRepository.findById(id);
    }

    /**
     * Зберегти дружбу.
     *
     * @param friendship дружба
     * @return збережена дружба
     */
    public Friendship saveFriendship(Friendship friendship) {
        return friendshipRepository.save(friendship);
    }

    /**
     * Видалити дружбу за її id.
     *
     * @param id ід дружби
     */
    public void deleteFriendship(Long id) {
        friendshipRepository.deleteById(id);
    }

    /**
     * Отримати список друзів для конкретного користувача.
     *
     * @param userId ід користувача
     * @return список друзів
     */
    public List<UserDTO> getFriendsOfUser(Long userId) {
        List<Friendship> friendships = friendshipRepository.findByUserIdOrFriendId(userId, userId);
        return friendships.stream()
                .map(friendship -> {
                    User friend = friendship.getUser().getId() == userId ? friendship.getFriend() : friendship.getUser();
                    return userService.convertToDTO(friend);
                })
                .toList();
    }

    /**
     * Отримати список спільних друзів для конкретного користувача.
     *
     * @param userId1 ід першого користувача
     * @param userId2 ід другого користувача
     * @return список спільних друзів
     */
    public List<UserDTO> getMutualFriends(Long userId1, Long userId2) {
        List<Friendship> friendships1 = friendshipRepository.findByUserIdOrFriendId(userId1, userId1);
        List<Friendship> friendships2 = friendshipRepository.findByUserIdOrFriendId(userId2, userId2);

        List<User> friends1 = friendships1.stream()
                .map(friendship -> friendship.getUser().getId() == userId1 ? friendship.getFriend() : friendship.getUser())
                .toList();

        List<User> friends2 = friendships2.stream()
                .map(friendship -> friendship.getUser().getId() == userId2 ? friendship.getFriend() : friendship.getUser())
                .toList();

        List<User> mutualFriends = friends1.stream()
                .filter(friends2::contains)
                .toList();

        return mutualFriends.stream()
                .map(userService::convertToDTO)
                .toList();
    }
}
