package com.sumdu.petrenko.diplom.services;

import com.sumdu.petrenko.diplom.dto.UserDTO;
import com.sumdu.petrenko.diplom.models.User;
import com.sumdu.petrenko.diplom.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервіс для пошуку користувачів.
 * <p>
 * Цей клас надає методи для пошуку користувачів за їх іменем або електронною поштою.
 * </p>
 */
@Service
public class SearchService {
    /**
     * Логер для сервісу пошуку.
     */
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(SearchService.class);

    /**
     * Репозиторій для роботи з користувачами.
     */
    private final UserRepository userRepository;

    /**
     * Конструктор сервісу пошуку.
     *
     * @param userRepository репозиторій для роботи з користувачами
     */
    @Autowired
    public SearchService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Пошук користувачів за їх іменем.
     *
     * @param nickname ім'я користувача
     * @return список користувачів з таким ім'ям
     */
    public List<UserDTO> searchUsersByNickname(String nickname) {
        logger.info("Пошук користувачів за іменем: {}", nickname);
        return userRepository.findByNicknameContainingIgnoreCase(nickname).stream()
                .map(this::convertToDTO)
                .toList();
    }

    /**
     * Перетворення об'єкта User в UserDTO.
     *
     * @param user об'єкт User
     * @return об'єкт UserDTO
     */
    private UserDTO convertToDTO(User user) {
        logger.info("Перетворення користувача в DTO: {}", user);
        return new UserDTO(user.getId(), user.getNickname(), user.getEmail());
    }

    /**
     * Пошук користувачів за їх іменем та електронною поштою.
     *
     * @param nickname ім'я користувача
     * @param email електронна пошта користувача
     * @return список користувачів з такою електронною поштою
     */
    public List<UserDTO> searchUsersByMultipleCriteria(String nickname, String email) {
        List<User> users = userRepository.findAll();
        logger.info("Пошук користувачів за іменем: {} та електронною поштою: {}", nickname, email);

        return users.stream()
                .filter(user -> (nickname == null || user.getNickname().contains(nickname)) &&
                        (email == null || user.getEmail().contains(email)))
                .map(this::convertToDTO)
                .toList();
    }
}