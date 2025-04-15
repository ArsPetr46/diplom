package com.sumdu.petrenko.diplom.services;

import com.sumdu.petrenko.diplom.dto.UserDTO;
import com.sumdu.petrenko.diplom.models.Friendship;
import com.sumdu.petrenko.diplom.models.User;
import com.sumdu.petrenko.diplom.repositories.FriendshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FriendshipService {
    private final FriendshipRepository friendshipRepository;
    private final UserService userService;

    @Autowired
    public FriendshipService(FriendshipRepository friendshipRepository, UserService userService) {
        this.friendshipRepository = friendshipRepository;
        this.userService = userService;
    }

    public Optional<Friendship> getFriendshipById(Long id) {
        return friendshipRepository.findById(id);
    }

    public Friendship saveFriendship(Friendship friendship) {
        return friendshipRepository.save(friendship);
    }

    public void deleteFriendship(Long id) {
        friendshipRepository.deleteById(id);
    }

    public List<UserDTO> getFriendsOfUser(Long userId) {
        List<Friendship> friendships = friendshipRepository.findByUserIdOrFriendId(userId, userId);
        return friendships.stream()
                .map(friendship -> {
                    User friend = friendship.getUser().getId() == userId ? friendship.getFriend() : friendship.getUser();
                    return userService.convertToDTO(friend);
                })
                .toList();
    }

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
