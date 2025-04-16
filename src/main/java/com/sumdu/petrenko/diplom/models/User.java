package com.sumdu.petrenko.diplom.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Модель для представлення користувача.
 * <p>
 * Цей клас використовується для зберігання інформації про користувачів у базі даних.
 * </p>
 */
@Entity
@NoArgsConstructor(force = true)
@Data
@Table(name = "users")
public class User {
    /**
     * Унікальний ідентифікатор користувача.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(name = "identifier", description = "Unique ID of a User",
            examples = {"1", "100", "3197"}, requiredMode = Schema.RequiredMode.AUTO,
            accessMode = Schema.AccessMode.READ_ONLY)
    private long id;

    /**
     * Унікальний нікнейм користувача.
     */
    @Column(unique = true, nullable = false)
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Nickname must contain only Latin letters and numbers")
    @Size(max = 30, message = "Nickname must be at most 30 characters long")
    @Schema(description = "Unique Nickname of a User", examples = {"Alex100", "7Joe7", "MyNickname"},
            requiredMode = Schema.RequiredMode.REQUIRED, accessMode = Schema.AccessMode.READ_WRITE)
    private String nickname;

    /**
     * Унікальний email користувача.
     */
    @Column(unique = true, nullable = false)
    @Email(message = "Email should be valid")
    @Size(max = 255, message = "Email must be at most 255 characters long")
    @Schema(description = "Unique Email of a User", example = "myemail@example.com", format = "email",
            requiredMode = Schema.RequiredMode.REQUIRED, accessMode = Schema.AccessMode.READ_WRITE)
    private String email;

    /**
     * Пароль користувача.
     */
    @Column(nullable = false)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]+$", message = "Password must be 8 characters long and contain at least one latin letter and one number")
    @Size(min = 8, max = 30, message = "Password must be between 8 and 30 characters long")
    @Schema(description = "Password of a User", examples = {"1234A", "B1C7D8", "Password123"},
            requiredMode = Schema.RequiredMode.REQUIRED, accessMode = Schema.AccessMode.WRITE_ONLY)
    private String password;

    /**
     * Опис користувача.
     */
    @Column
    @Size(max = 300, message = "User description must be at most 300 characters long")
    @Schema(description = "A personal description that user can write about himself", example = "This is Andy's account. I like to play football and read books.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, accessMode = Schema.AccessMode.READ_WRITE, hidden = true)
    private String userDescription;

    /**
     * Дата народження користувача.
     */
    @Column
    @Schema(description = "User's birthdate", example = "2025-02-25",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, accessMode = Schema.AccessMode.READ_WRITE)
    private LocalDate birthDate;

    /**
     * URL до аватарки користувача.
     */
    @Column
    @Schema(description = "URL to User's profile picture", example = "https://example.com/avatar.jpg",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, accessMode = Schema.AccessMode.READ_WRITE)
    private String avatarUrl;

    /**
     * Час створення користувача.
     */
    @Column(nullable = false)
    @Schema(description = "URL to User's profile picture", example = "2025-02-25T15:30:00",
            requiredMode = Schema.RequiredMode.AUTO, accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime userCreationTime;

    /**
     * Друзі користувача.
     */
    @OneToMany(mappedBy = "user")
    private Set<Friendship> friends;

    /**
     * Запити на дружбу, які надіслав користувач.
     */
    @OneToMany(mappedBy = "friend")
    private Set<Friendship> friendOf;

    /**
     * Запити на дружбу, які отримав користувач.
     */
    @OneToMany(mappedBy = "sender")
    private Set<FriendRequest> sentRequests;

    /**
     * Запити на дружбу, які отримав користувач.
     */
    @OneToMany(mappedBy = "receiver")
    private Set<FriendRequest> receivedRequests;

    /**
     * Конструктор для створення користувача.
     *
     * @param id                Унікальний ідентифікатор користувача.
     * @param nickname          Унікальний нікнейм користувача.
     * @param email             Унікальний email користувача.
     * @param password          Пароль користувача.
     * @param userDescription   Опис користувача.
     * @param birthDate         Дата народження користувача.
     * @param avatarUrl         URL до аватарки користувача.
     * @param userCreationTime  Час створення користувача.
     */
    public User(long id, String nickname, String email,
                String password, String userDescription, LocalDate birthDate,
                String avatarUrl, LocalDateTime userCreationTime) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.userDescription = userDescription;
        this.birthDate = birthDate;
        this.avatarUrl = avatarUrl;
        this.userCreationTime = userCreationTime;
    }

    /**
     * Конструктор для створення користувача.
     *
     * @param nickname Унікальний нікнейм користувача.
     * @param email    Унікальний email користувача.
     * @param password Пароль користувача.
     */
    public User(String nickname, String email, String password) {
        this.id = 0;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
    }

    /**
     * Конструктор для створення користувача.
     *
     * @param id       Унікальний ідентифікатор користувача.
     * @param nickname Унікальний нікнейм користувача.
     * @param email    Унікальний email користувача.
     * @param password Пароль користувача.
     */
    public User(Long id, String nickname, String email, String password) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
    }

    /**
     * Метод, який викликається перед збереженням користувача в базі даних.
     */
    @PrePersist
    protected void onCreate() {
        this.userCreationTime = LocalDateTime.now();
    }
}
