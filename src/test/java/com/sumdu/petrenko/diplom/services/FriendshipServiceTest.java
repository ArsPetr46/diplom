package com.sumdu.petrenko.diplom.services;

import com.sumdu.petrenko.diplom.dto.UserDTO;
import com.sumdu.petrenko.diplom.models.Friendship;
import com.sumdu.petrenko.diplom.models.User;
import com.sumdu.petrenko.diplom.repositories.FriendshipRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class FriendshipServiceTest {

    @Mock
    private FriendshipRepository friendshipRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private FriendshipService friendshipService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

//    @Test
//    public void testGetFriendsOfUser() {
//        User user = new User("userNickname", "user@example.com", "Password1");
//        User friend1 = new User("friend1Nickname", "friend1@example.com", "Password2");
//        User friend2 = new User("friend2Nickname", "friend2@example.com", "Password3");
//
//
//
//        Friendship friendship1 = new Friendship(user, friend1);
//
//        Friendship friendship2 = new Friendship(user, friend2);
//
//        when(friendshipRepository.findByUserIdOrFriendId(userId, userId)).thenReturn(Arrays.asList(friendship1, friendship2));
//        when(userService.convertToDTO(friend1)).thenReturn(new UserDTO(friend1.getId(), friend1.getNickname(), friend1.getEmail()));
//        when(userService.convertToDTO(friend2)).thenReturn(new UserDTO(friend2.getId(), friend2.getNickname(), friend2.getEmail()));
//
//        List<UserDTO> friends = friendshipService.getFriendsOfUser(userId);
//
//        assertEquals(2, friends.size());
//        assertEquals(friend1.getId(), friends.get(0).getId());
//        assertEquals(friend2.getId(), friends.get(1).getId());
//    }
//
//    @Test
//    public void testGetFriendsOfUser_NoFriends() {
//        Long userId = 1L;
//
//        when(friendshipRepository.findByUserIdOrFriendId(userId, userId)).thenReturn(Arrays.asList());
//
//        List<UserDTO> friends = friendshipService.getFriendsOfUser(userId);
//
//        assertEquals(0, friends.size());
//    }
//
//    @Test
//    public void testGetMutualFriends() {
//        Long userId1 = 1L;
//        Long userId2 = 2L;
//
//        User user1 = new User();
//        user1.setId(1L);
//        User user2 = new User();
//        user2.setId(2L);
//        User mutualFriend = new User();
//        mutualFriend.setId(3L);
//
//        Friendship friendship1 = new Friendship();
//        friendship1.setUser(user1);
//        friendship1.setFriend(mutualFriend);
//
//        Friendship friendship2 = new Friendship();
//        friendship2.setUser(user2);
//        friendship2.setFriend(mutualFriend);
//
//        when(friendshipRepository.findByUserIdOrFriendId(userId1, userId1)).thenReturn(Arrays.asList(friendship1));
//        when(friendshipRepository.findByUserIdOrFriendId(userId2, userId2)).thenReturn(Arrays.asList(friendship2));
//        when(userService.convertToDTO(mutualFriend)).thenReturn(new UserDTO(mutualFriend.getId(), mutualFriend.getNickname(), mutualFriend.getEmail()));
//
//        List<UserDTO> mutualFriends = friendshipService.getMutualFriends(userId1, userId2);
//
//        assertEquals(1, mutualFriends.size());
//        assertEquals(mutualFriend.getId(), mutualFriends.get(0).getId());
//    }
//
//    @Test
//    public void testGetMutualFriends_NoMutualFriends() {
//        Long userId1 = 1L;
//        Long userId2 = 2L;
//
//        User user1 = new User();
//        user1.setId(1L);
//        User user2 = new User();
//        user2.setId(2L);
//        User friend1 = new User();
//        friend1.setId(3L);
//        User friend2 = new User();
//        friend2.setId(4L);
//
//        Friendship friendship1 = new Friendship();
//        friendship1.setUser(user1);
//        friendship1.setFriend(friend1);
//
//        Friendship friendship2 = new Friendship();
//        friendship2.setUser(user2);
//        friendship2.setFriend(friend2);
//
//        when(friendshipRepository.findByUserIdOrFriendId(userId1, userId1)).thenReturn(Arrays.asList(friendship1));
//        when(friendshipRepository.findByUserIdOrFriendId(userId2, userId2)).thenReturn(Arrays.asList(friendship2));
//
//        List<UserDTO> mutualFriends = friendshipService.getMutualFriends(userId1, userId2);
//
//        assertEquals(0, mutualFriends.size());
//    }
}
