package com.sumdu.petrenko.diplom.services;

import com.sumdu.petrenko.diplom.models.FriendRequest;
import com.sumdu.petrenko.diplom.models.Friendship;
import com.sumdu.petrenko.diplom.repositories.FriendRequestRepository;
import com.sumdu.petrenko.diplom.repositories.FriendshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FriendRequestService {
    private final FriendRequestRepository friendRequestRepository;
    private final FriendshipRepository friendshipRepository;

    @Autowired
    public FriendRequestService(FriendRequestRepository friendRequestRepository, FriendshipRepository friendshipRepository) {
        this.friendRequestRepository = friendRequestRepository;
        this.friendshipRepository = friendshipRepository;
    }

    public Optional<FriendRequest> getFriendRequestById(Long id) {
        return friendRequestRepository.findById(id);
    }

    public List<FriendRequest> getFriendRequestsBySenderId(Long senderId) {
        return friendRequestRepository.findBySenderId(senderId);
    }

    public List<FriendRequest> getFriendRequestsByReceiverId(Long receiverId) {
        return friendRequestRepository.findByReceiverId(receiverId);
    }

    public FriendRequest saveFriendRequest(FriendRequest friendRequest) {
        return friendRequestRepository.save(friendRequest);
    }

    public void deleteFriendRequest(Long id) {
        friendRequestRepository.deleteById(id);
    }

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
        } else {
            throw new IllegalArgumentException("Friend request not found");
        }
    }

    public void cancelFriendRequest(Long requestId) {
        if (friendRequestRepository.existsById(requestId)) {
            friendRequestRepository.deleteById(requestId);
        } else {
            throw new IllegalArgumentException("Friend request not found");
        }
    }

    public String getFriendshipStatus(Long userId1, Long userId2) {
        Optional<FriendRequest> sentRequest = friendRequestRepository.findBySenderIdAndReceiverId(userId1, userId2);
        Optional<FriendRequest> receivedRequest = friendRequestRepository.findBySenderIdAndReceiverId(userId2, userId1);

        if (sentRequest.isPresent()) {
            return "Pending";
        } else if (receivedRequest.isPresent()) {
            return "Received";
        } else {
            Optional<Friendship> friendship = friendshipRepository.findByUserIdAndFriendId(userId1, userId2);
            if (friendship.isPresent()) {
                return "Friends";
            } else {
                return "Not Friends";
            }
        }
    }
}