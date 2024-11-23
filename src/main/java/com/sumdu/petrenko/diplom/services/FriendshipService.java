package com.sumdu.petrenko.diplom.services;

import com.sumdu.petrenko.diplom.dto.UserDTO;
import com.sumdu.petrenko.diplom.models.Friendship;
import com.sumdu.petrenko.diplom.models.User;
import com.sumdu.petrenko.diplom.repositories.FriendshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FriendshipService {
    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private UserService userService;

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
                .collect(Collectors.toList());
    }
}
