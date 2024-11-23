package com.sumdu.petrenko.diplom.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "friendships")
public class Friendship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "friend_id")
    private User friend;

    @PrePersist
    @PreUpdate
    private void validateFriendship() {
        if (user.getId() == friend.getId()) {
            throw new IllegalArgumentException("User cannot be friends with themselves.");
        }

        if (user.getId() > friend.getId()) {
            User temp = user;
            user = friend;
            friend = temp;
        }
    }
}
