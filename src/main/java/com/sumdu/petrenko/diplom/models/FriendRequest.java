package com.sumdu.petrenko.diplom.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "friend_requests")
public class FriendRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @PrePersist
    @PreUpdate
    private void validateFriendRequest() {
        if (sender.getId() == receiver.getId()) {
            throw new IllegalArgumentException("Sender cannot send a friend request to themselves.");
        }
    }
}
