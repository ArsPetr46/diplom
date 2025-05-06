package com.sumdu.petrenko.diplom.services;

import com.sumdu.petrenko.diplom.microservices.friendships.models.FriendRequestEntity;
import com.sumdu.petrenko.diplom.microservices.friendships.models.FriendshipEntity;
import com.sumdu.petrenko.diplom.microservices.friendships.services.FriendRequestService;
import com.sumdu.petrenko.diplom.microservices.users.models.UserEntity;
import com.sumdu.petrenko.diplom.microservices.friendships.repositories.FriendRequestRepository;
import com.sumdu.petrenko.diplom.microservices.friendships.repositories.FriendshipRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class FriendRequestEntityServiceTest {

    @Mock
    private FriendRequestRepository friendRequestRepository;

    @Mock
    private FriendshipRepository friendshipRepository;

    @InjectMocks
    private FriendRequestService friendRequestService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetFriendshipStatus_Pending() {
        Long userId1 = 1L;
        Long userId2 = 2L;

        FriendRequestEntity friendRequestEntity = new FriendRequestEntity();
        when(friendRequestRepository.findBySenderIdAndReceiverId(userId1, userId2)).thenReturn(Optional.of(friendRequestEntity));

        String status = friendRequestService.getFriendshipStatus(userId1, userId2);

        assertEquals("Pending", status);
    }

    @Test
    public void testGetFriendshipStatus_Received() {
        Long userId1 = 1L;
        Long userId2 = 2L;

        FriendRequestEntity friendRequestEntity = new FriendRequestEntity();
        when(friendRequestRepository.findBySenderIdAndReceiverId(userId2, userId1)).thenReturn(Optional.of(friendRequestEntity));

        String status = friendRequestService.getFriendshipStatus(userId1, userId2);

        assertEquals("Received", status);
    }

    @Test
    public void testGetFriendshipStatus_Friends() {
        Long userId1 = 1L;
        Long userId2 = 2L;

        FriendshipEntity friendshipEntity = new FriendshipEntity();
        when(friendshipRepository.findByUserIdAndFriendId(userId1, userId2)).thenReturn(Optional.of(friendshipEntity));

        String status = friendRequestService.getFriendshipStatus(userId1, userId2);

        assertEquals("Friends", status);
    }

    @Test
    public void testGetFriendshipStatus_NotFriends() {
        Long userId1 = 1L;
        Long userId2 = 2L;

        when(friendRequestRepository.findBySenderIdAndReceiverId(userId1, userId2)).thenReturn(Optional.empty());
        when(friendRequestRepository.findBySenderIdAndReceiverId(userId2, userId1)).thenReturn(Optional.empty());
        when(friendshipRepository.findByUserIdAndFriendId(userId1, userId2)).thenReturn(Optional.empty());

        String status = friendRequestService.getFriendshipStatus(userId1, userId2);

        assertEquals("Not Friends", status);
    }

    @Test
    public void testAcceptFriendRequest_Success() {
        Long requestId = 1L;
        FriendRequestEntity friendRequestEntity = new FriendRequestEntity(new UserEntity(), new UserEntity());

        when(friendRequestRepository.findById(requestId)).thenReturn(Optional.of(friendRequestEntity));

        friendRequestService.acceptFriendRequest(requestId);

        verify(friendshipRepository, times(1)).save(any(FriendshipEntity.class));
        verify(friendRequestRepository, times(1)).deleteById(requestId);
    }

    @Test
    public void testAcceptFriendRequest_FriendRequestNotFound() {
        Long requestId = 1L;

        when(friendRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> friendRequestService.acceptFriendRequest(requestId));

        verify(friendshipRepository, never()).save(any(FriendshipEntity.class));
        verify(friendRequestRepository, never()).deleteById(requestId);
    }
}
