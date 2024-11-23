package com.sumdu.petrenko.diplom.repositories;

import com.sumdu.petrenko.diplom.models.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    List<Friendship> findByUserIdOrFriendId(Long userId, Long friendId);

    Optional<Friendship> findByUserIdAndFriendId(Long userId1, Long userId2);
}