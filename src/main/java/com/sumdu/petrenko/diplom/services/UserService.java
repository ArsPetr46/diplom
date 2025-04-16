package com.sumdu.petrenko.diplom.services;

import com.sumdu.petrenko.diplom.dto.UserDTO;
import com.sumdu.petrenko.diplom.models.User;
import com.sumdu.petrenko.diplom.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Сервіс для роботи з користувачами.
 * <p>
 * Цей клас надає методи для обробки користувачів, включаючи їх створення, видалення та отримання списків користувачів.
 * </p>
 */
@Service
public class UserService {
    /**
     * Репозиторій для роботи з користувачами.
     */
    private final UserRepository userRepository;

    /**
     * Конструктор сервісу користувачів.
     *
     * @param userRepository репозиторій для роботи з користувачами
     */
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Отримати всіх користувачів.
     *
     * @return список всіх користувачів
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Отримати користувача за його id.
     *
     * @param id ід користувача
     * @return користувач
     */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Зберегти користувача.
     *
     * @param user користувач
     * @return збережений користувач
     */
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    /**
     * Видалити користувача за його id.
     *
     * @param id ід користувача
     */
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * Перетворення об'єкта User в UserDTO.
     *
     * @param user об'єкт User
     * @return об'єкт UserDTO
     */
    public UserDTO convertToDTO(User user) {
        return new UserDTO(user.getId(), user.getNickname(), user.getEmail());
    }

    /**
     * Отримати всіх користувачів у вигляді DTO.
     *
     * @return список всіх користувачів у вигляді DTO
     */
    public Optional<List<UserDTO>> getAllUsersAsDTO() {
        return Optional.of(userRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList());
    }

    /**
     * Отримати користувача за його id у вигляді DTO.
     *
     * @param id ід користувача
     * @return користувач у вигляді DTO
     */
    public Optional<UserDTO> getUserByIdAsDTO(Long id) {
        return userRepository.findById(id)
                .map(this::convertToDTO);
    }
}
