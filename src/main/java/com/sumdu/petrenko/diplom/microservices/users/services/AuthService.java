package com.sumdu.petrenko.diplom.microservices.users.services;

import com.sumdu.petrenko.diplom.dto.UserDTO;
import com.sumdu.petrenko.diplom.microservices.users.models.AuthRequest;
import com.sumdu.petrenko.diplom.microservices.users.models.AuthResponse;
import com.sumdu.petrenko.diplom.microservices.users.models.UserEntity;
import com.sumdu.petrenko.diplom.microservices.users.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Сервіс для автентифікації користувачів.
 * <p>
 * Цей сервіс відповідає за автентифікацію користувачів, перевірку їх облікових даних
 * та генерацію JWT токенів для успішно автентифікованих користувачів.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    /**
     * Репозиторій для роботи з користувачами.
     */
    private final UserRepository userRepository;

    /**
     * Кодувальник паролів для перевірки паролів користувачів.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Сервіс для роботи з JWT токенами.
     */
    private final JwtService jwtService;

    /**
     * Автентифікувати користувача за його email та паролем.
     * <p>
     * Метод перевіряє наявність користувача з вказаним email,
     * порівнює пароль та, у разі успіху, генерує JWT токен.
     * </p>
     *
     * @param authRequest Запит автентифікації з email та паролем
     * @return Відповідь з JWT токеном та даними користувача
     * @throws BadCredentialsException якщо автентифікація не вдалася
     */
    public AuthResponse authenticate(AuthRequest authRequest) {
        UserEntity user = userRepository.findByEmail(authRequest.getEmail())
                .orElseThrow(() -> {
                    log.warn("Спроба автентифікації з неіснуючим email: {}", authRequest.getEmail());
                    return new BadCredentialsException("Невірний email або пароль");
                });

        if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            log.warn("Невірний пароль для користувача з email: {}", authRequest.getEmail());
            throw new BadCredentialsException("Невірний email або пароль");
        }

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities("USER")
                .build();

        String token = jwtService.generateToken(userDetails);

        log.info("Успішна автентифікація користувача: {}", user.getEmail());

        return new AuthResponse(token, user.getId());
    }

    /**
     * Зберегти нового користувача.
     * <p>
     * Метод створює нового користувача в системі. Перед збереженням рекомендується
     * перевірити унікальність email та нікнейму на рівні контролера.
     * </p>
     * <p>
     * Метод виконується в межах транзакції для забезпечення атомарності операції.
     * </p>
     *
     * @param userEntity об'єкт користувача для збереження
     * @return DTO збереженого користувача з присвоєним ID
     */
    @Transactional
    public AuthResponse register(UserEntity userEntity) {
        try {
            String encodedPassword = passwordEncoder.encode(userEntity.getPassword());
            userEntity.setPassword(encodedPassword);

            UserEntity savedUser = userRepository.save(userEntity);
            log.info("Створено нового користувача з ID: {}", savedUser.getId());

            UserDetails userDetails = org.springframework.security.core.userdetails.User
                    .withUsername(savedUser.getEmail())
                    .password(savedUser.getPassword())
                    .authorities("USER")
                    .build();

            String token = jwtService.generateToken(userDetails);

            return new AuthResponse(token, savedUser.getId());
        } catch (DataAccessException e) {
            log.error("Помилка доступу до БД при збереженні користувача: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Непередбачена помилка при збереженні користувача: {}", e.getMessage());
            throw e;
        }
    }
}
