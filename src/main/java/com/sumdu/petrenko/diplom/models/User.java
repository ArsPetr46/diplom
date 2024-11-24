package com.sumdu.petrenko.diplom.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import jakarta.validation.constraints.Pattern;

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
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Nickname must contain only Latin letters and numbers")
    private String nickname;

    @Column(unique = true, nullable = false)
    @Getter
    @Setter
    @Email(message = "Email should be valid")
    private String email;

    @Column(nullable = false)
    @Getter
    @Setter
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*?&]+$", message = "Password must contain Latin letters, at least one number, and can contain special symbols")
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
