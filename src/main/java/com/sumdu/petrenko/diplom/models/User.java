package com.sumdu.petrenko.diplom.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private long id;

    @Column(unique = true, nullable = false)
    @Getter
    @Setter
    private String nickname;

    @Column(unique = true, nullable = false)
    @Getter
    @Setter
    private String email;

    @Column(nullable = false)
    @Getter
    @Setter
    private String password;

    @OneToMany(mappedBy = "user")
    private Set<Friendship> friends;

    @OneToMany(mappedBy = "friend")
    private Set<Friendship> friendOf;

    @OneToMany(mappedBy = "sender")
    private Set<FriendRequest> sentRequests;

    @OneToMany(mappedBy = "receiver")
    private Set<FriendRequest> receivedRequests;
}
