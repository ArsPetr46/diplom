package com.sumdu.petrenko.diplom.mappers;

import com.sumdu.petrenko.diplom.dto.UserDTO;
import com.sumdu.petrenko.diplom.microservices.users.models.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Маппер для конвертації між сутністю користувача та DTO.
 * <p>
 * Цей клас відповідає за перетворення сутності {@link UserEntity} на об'єкт передачі даних
 * {@link UserDTO}, який використовується для передачі інформації про користувача через API.
 * </p>
 * <p>
 * Маппер забезпечує розділення внутрішньої моделі даних від моделі, яка експортується
 * зовнішнім клієнтам, та реалізує принцип інкапсуляції даних.
 * </p>
 */
@Component
@Slf4j
public class UserMapper {
    /**
     * Перетворює сутність користувача в DTO.
     * <p>
     * Метод створює об'єкт {@link UserDTO} з даними з сутності {@link UserEntity}.
     * Виключає чутливі дані, такі як пароль та email, з результуючого DTO.
     * </p>
     * <p>
     * Якщо вхідний об'єкт сутності є null, результат також буде null.
     * </p>
     *
     * @param user сутність користувача для конвертації
     * @return DTO користувача або null, якщо вхідний параметр є null
     */
    public UserDTO toUserDTO(UserEntity user) {
        if (user == null) {
            log.debug("Спроба конвертації null користувача в DTO");
            return null;
        }

        try {
            UserDTO userDTO = new UserDTO(
                    user.getId(),
                    user.getNickname(),
                    user.getUserDescription(),
                    user.getBirthDate(),
                    user.getAvatarUrl(),
                    user.getUserCreationDate()
            );
            log.debug("Успішно сконвертовано користувача з ID {} в DTO", user.getId());
            return userDTO;
        } catch (Exception e) {
            log.error("Помилка при конвертації користувача з ID {} в DTO: {}",
                    user.getId(), e.getMessage(), e);
            throw new RuntimeException("Помилка конвертації користувача в DTO", e);
        }
    }

    /**
     * Перетворює колекцію сутностей користувачів у колекцію DTO.
     * <p>
     * Метод обробляє кожну сутність з вхідної колекції і створює відповідний DTO.
     * Якщо під час конвертації окремої сутності виникає помилка, вона логується,
     * але не перериває процес конвертації інших сутностей.
     * </p>
     *
     * @param users колекція сутностей користувачів для конвертації
     * @return колекція DTO користувачів
     */
    public List<UserDTO> toUserDTOList(List<UserEntity> users) {
        if (users == null) {
            log.debug("Спроба конвертації null списку користувачів в DTO");
            return Collections.emptyList();
        }

        try {
            List<UserDTO> result = new ArrayList<>(users.size());

            for (UserEntity user : users) {
                try {
                    UserDTO dto = toUserDTO(user);
                    if (dto != null) {
                        result.add(dto);
                    }
                } catch (Exception e) {
                    log.error("Помилка при конвертації користувача в DTO: {}", e.getMessage());
                }
            }

            log.info("Успішно сконвертовано {} з {} сутностей користувачів в DTO",
                    result.size(), users.size());
            return result;
        } catch (Exception e) {
            log.error("Критична помилка при конвертації списку користувачів в DTO: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}