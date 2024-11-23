package com.sumdu.petrenko.diplom.services;

import com.sumdu.petrenko.diplom.models.FriendRequest;
import com.sumdu.petrenko.diplom.models.Friendship;
import com.sumdu.petrenko.diplom.models.User;
import com.sumdu.petrenko.diplom.repositories.FriendRequestRepository;
import com.sumdu.petrenko.diplom.repositories.FriendshipRepository;
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

public class FriendRequestServiceTest {

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

        FriendRequest friendRequest = new FriendRequest();
        when(friendRequestRepository.findBySenderIdAndReceiverId(userId1, userId2)).thenReturn(Optional.of(friendRequest));

        String status = friendRequestService.getFriendshipStatus(userId1, userId2);

        assertEquals("Pending", status);
    }

    @Test
    public void testGetFriendshipStatus_Received() {
        Long userId1 = 1L;
        Long userId2 = 2L;

        FriendRequest friendRequest = new FriendRequest();
        when(friendRequestRepository.findBySenderIdAndReceiverId(userId2, userId1)).thenReturn(Optional.of(friendRequest));

        String status = friendRequestService.getFriendshipStatus(userId1, userId2);

        assertEquals("Received", status);
    }

    @Test
    public void testGetFriendshipStatus_Friends() {
        Long userId1 = 1L;
        Long userId2 = 2L;

        Friendship friendship = new Friendship();
        when(friendshipRepository.findByUserIdAndFriendId(userId1, userId2)).thenReturn(Optional.of(friendship));

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
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setId(requestId);
        friendRequest.setSender(new User());
        friendRequest.setReceiver(new User());

        when(friendRequestRepository.findById(requestId)).thenReturn(Optional.of(friendRequest));

        friendRequestService.acceptFriendRequest(requestId);

        verify(friendshipRepository, times(1)).save(any(Friendship.class));
        verify(friendRequestRepository, times(1)).deleteById(requestId);
    }

    @Test
    public void testAcceptFriendRequest_FriendRequestNotFound() {
        Long requestId = 1L;

        when(friendRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> friendRequestService.acceptFriendRequest(requestId));

        verify(friendshipRepository, never()).save(any(Friendship.class));
        verify(friendRequestRepository, never()).deleteById(requestId);
    }
}
