package com.sumdu.petrenko.diplom.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(force = true)
@Data
@Table(name = "friend_requests")
public class FriendRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique ID of a Friend Request between two Users",
            examples = {"1", "100", "3197"}, requiredMode = Schema.RequiredMode.AUTO,
            accessMode = Schema.AccessMode.READ_ONLY)
    private long id = 0;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    @Schema(description = "The User who has sent a Friend Request", requiredMode = Schema.RequiredMode.REQUIRED,
            accessMode = Schema.AccessMode.READ_ONLY)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    @Schema(description = "The User who has to answer a Friend Request", requiredMode = Schema.RequiredMode.REQUIRED,
            accessMode = Schema.AccessMode.READ_ONLY)
    private User receiver;

    public FriendRequest(User sender, User receiver) {
        if (sender.getId() == receiver.getId()) {
            throw new IllegalArgumentException("Sender cannot send a friend request to themselves.");
        }

        if (sender.getId() < receiver.getId()) {
            this.sender = receiver;
            this.receiver = sender;
        } else {
            this.sender = sender;
            this.receiver = receiver;
        }
    }
}
