package com.sumdu.petrenko.diplom.repositories;

import com.sumdu.petrenko.diplom.models.FriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    List<FriendRequest> findBySenderId(Long senderId);

    List<FriendRequest> findByReceiverId(Long receiverId);
}
