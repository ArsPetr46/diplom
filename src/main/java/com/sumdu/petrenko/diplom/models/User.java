package com.sumdu.petrenko.diplom.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
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
    @Size(max = 30, message = "Nickname must be at most 30 characters long")
    private String nickname;

    @Column(unique = true, nullable = false)
    @Getter
    @Setter
    @Email(message = "Email should be valid")
    @Size(max = 255, message = "Email must be at most 255 characters long")
    private String email;

    @Column(nullable = false)
    @Getter
    @Setter
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]+$", message = "Password must contain Latin letters and at least one number")
    @Size(max = 30, message = "Password must be at most 30 characters long")
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
