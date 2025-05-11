package com.sumdu.petrenko.diplom.microservices.users.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;

/**
 * Модель для представлення користувача.
 * <p>
 * Цей клас є сутністю JPA для зберігання інформації про користувачів у базі даних.
 * Використовується для операцій створення, зчитування, оновлення та видалення користувачів.
 * </p>
 */
@Entity
@NoArgsConstructor(force = true)
@Data
@Table(name = "users", schema = "user_service")
public class UserEntity {
    /**
     * Унікальний ідентифікатор користувача.
     * Генерується автоматично при збереженні в базу даних.
     * Не може бути змінений після створення.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    @Null(message = "ID користувача потрібно надавати при створенні")
    @Schema(description = "Унікальний ID користувача",
            examples = {"1", "100", "3197"},
            requiredMode = Schema.RequiredMode.AUTO,
            accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    /**
     * Унікальний нікнейм користувача.
     * Має бути унікальним у системі, містити лише латинські літери та цифри,
     * та бути не довшим за 30 символів.
     */
    @Column(unique = true, nullable = false)
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Нікнейм повинен містити лише латинські літери та цифри")
    @Size(min = 5, max = 30, message = "Нікнейм повинен бути довше 5 символів та не перевищувати 30 символів")
    @Schema(description = "Унікальний нікнейм користувача", examples = {"Alex100", "7Joe7", "MyNickname"},
            requiredMode = Schema.RequiredMode.REQUIRED, accessMode = Schema.AccessMode.READ_WRITE)
    private String nickname;

    /**
     * Унікальний email користувача.
     * Має бути унікальним у системі та відповідати формату електронної пошти.
     * Не може бути змінений після створення користувача.
     */
    @Column(unique = true, nullable = false, updatable = false)
    @Email(message = "Email повинен бути валідним")
    @Size(max = 255, message = "Email не повинен перевищувати 255 символів")
    @Schema(description = "Унікальна електронна пошта користувача", example = "myemail@example.com", format = "email",
            requiredMode = Schema.RequiredMode.REQUIRED, accessMode = Schema.AccessMode.READ_ONLY)
    private final String email;

    /**
     * Пароль користувача.
     * Повинен містити щонайменше 8 символів, включаючи хоча б одну латинську літеру та одну цифру.
     * Зберігається в зашифрованому вигляді.
     */
    @Column(nullable = false)
//    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]+$", message = "Пароль повинен складатися з латинських літер і цифр")
//    @Size(min = 8, max = 30, message = "Пароль повинен бути від 8 до 30 символів довжиною")
    @Schema(description = "Зашифрований пароль користувача",
            requiredMode = Schema.RequiredMode.REQUIRED, accessMode = Schema.AccessMode.WRITE_ONLY)
    private String password;

    /**
     * Опис користувача.
     * Необов'язкове поле, що містить додаткову інформацію про користувача.
     * Максимальна довжина - 300 символів.
     */
    @Column
    @Size(max = 300, message = "Опис користувача не повинен перевищувати 300 символів")
    @Schema(description = "Персональний опис, який користувач може написати про себе",
            example = "Мій обліковий запис. Люблю грати футбол та читати книги.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, accessMode = Schema.AccessMode.READ_WRITE)
    private String userDescription;

    /**
     * Дата народження користувача.
     * Необов'язкове поле.
     * Використовується для вікової верифікації та персоналізації контенту.
     */
    @Column
    @Past(message = "Дата народження повинна бути в минулому")
    @Schema(description = "Дата народження користувача", example = "2000-01-01",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, accessMode = Schema.AccessMode.READ_WRITE)
    private LocalDate birthDate;

    /**
     * URL до аватарки користувача.
     * Необов'язкове поле.
     * Має бути валідним URL-посиланням на зображення.
     */
    @Column
    @URL(message = "URL аватара повинен бути валідним URL-адресою")
    @Schema(description = "URL до фото профілю користувача", example = "https://example.com/avatar.jpg",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, accessMode = Schema.AccessMode.READ_WRITE)
    private String avatarUrl;

    /**
     * Час створення облікового запису користувача.
     * Встановлюється автоматично при створенні користувача.
     * Не може бути змінений після створення.
     */
    @Column(nullable = false, updatable = false)
    @Schema(description = "Час створення облікового запису користувача", example = "2023-05-15",
            requiredMode = Schema.RequiredMode.AUTO, accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDate userCreationDate;

    /**
     * Конструктор для створення користувача з основними даними.
     * Використовується при мінімальній реєстрації користувача.
     *
     * @param nickname Унікальний нікнейм користувача.
     * @param email    Унікальна електронна пошта користувача.
     * @param password Пароль користувача (буде зашифрований перед збереженням).
     */
    public UserEntity(String nickname, String email, String password) {
        this.nickname = nickname;
        this.email = email;
        this.password = password;
    }

    /**
     * Конструктор для створення користувача з усіма полями.
     * Використовується при повній реєстрації з додатковими даними.
     *
     * @param nickname        Унікальний нікнейм користувача.
     * @param email           Унікальна електронна пошта користувача.
     * @param password        Пароль користувача (буде зашифрований перед збереженням).
     * @param userDescription Опис користувача (необов'язково).
     * @param birthDate       Дата народження користувача (необов'язково).
     * @param avatarUrl       URL до фото профілю користувача (необов'язково).
     */
    public UserEntity(String nickname, String email, String password, String userDescription, LocalDate birthDate, String avatarUrl) {
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.userDescription = userDescription;
        this.birthDate = birthDate;
        this.avatarUrl = avatarUrl;
    }

    /**
     * Метод життєвого циклу, який викликається перед збереженням нового користувача в базу даних.
     * Встановлює поточний час як час створення облікового запису.
     */
    @PrePersist
    protected void onCreate() {
        this.userCreationDate = LocalDate.now();
    }
}
