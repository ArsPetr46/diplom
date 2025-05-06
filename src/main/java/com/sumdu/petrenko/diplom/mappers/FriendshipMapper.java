package com.sumdu.petrenko.diplom.mappers;

import com.sumdu.petrenko.diplom.dto.FriendshipDTO;
import com.sumdu.petrenko.diplom.microservices.friendships.models.FriendshipEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Маппер для конвертації між сутністю дружби та DTO.
 * <p>
 * Цей клас відповідає за перетворення двонаправленої сутності {@link FriendshipEntity}
 * на односторонній об'єкт передачі даних {@link FriendshipDTO}, враховуючи перспективу
 * конкретного користувача.
 * </p>
 * <p>
 * Особливість маппера полягає в тому, що сутність дружби представляє відносини між
 * двома користувачами, але DTO представляє ці відносини з точки зору одного користувача.
 * </p>
 */
@Component
@Slf4j
public class FriendshipMapper {
    /**
     * Перетворює сутність дружби в DTO з точки зору конкретного користувача.
     * <p>
     * Метод аналізує відносини дружби та визначає, якою стороною є вказаний користувач
     * (першою чи другою), після чого створює DTO з правильним визначенням друга та
     * станів блокування.
     * </p>
     *
     * @param entity сутність дружби для конвертації
     * @param userId ID користувача, з точки зору якого робиться перетворення
     * @return DTO дружби або null у випадку помилки
     * @throws IllegalArgumentException якщо вказаний користувач не є стороною в цих відносинах дружби
     */
    public FriendshipDTO toDto(FriendshipEntity entity, Long userId) {
        if (entity == null) {
            log.debug("Спроба конвертації null сутності дружби в DTO");
            return null;
        }

        try {
            if (!entity.getUserId1().equals(userId) && !entity.getUserId2().equals(userId)) {
                log.warn("Спроба конвертації дружби, де користувач {} не є учасником (учасники: {}, {})",
                        userId, entity.getUserId1(), entity.getUserId2());
                throw new IllegalArgumentException("Користувач не є стороною в цих відносинах дружби");
            }

            boolean isCurrentUserFirst = entity.getUserId1().equals(userId);

            FriendshipDTO dto = new FriendshipDTO(
                    getFriendId(entity, userId),
                    isCurrentUserFirst ? entity.isBlockedByUser1() : entity.isBlockedByUser2(),
                    isCurrentUserFirst ? entity.isBlockedByUser2() : entity.isBlockedByUser1(),
                    entity.getCreatedAt()
            );

            log.debug("Успішно сконвертовано дружбу між користувачами {} і {} в DTO з точки зору користувача {}",
                    entity.getUserId1(), entity.getUserId2(), userId);
            return dto;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Помилка при конвертації дружби в DTO: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Отримує ID друга в залежності від того, хто запитує.
     * <p>
     * Метод визначає, якою стороною в відносинах дружби є вказаний користувач,
     * і повертає ID іншої сторони.
     * </p>
     *
     * @param entity сутність дружби
     * @param userId ID користувача, для якого потрібно отримати ID друга
     * @return ID друга
     * @throws IllegalArgumentException якщо вказаний користувач не є стороною в цих відносинах дружби
     */
    public Long getFriendId(FriendshipEntity entity, Long userId) {
        if (entity == null || userId == null) {
            log.warn("Спроба отримати ID друга з null сутності дружби або з null ID користувача");
            throw new IllegalArgumentException("Сутність дружби або ID користувача не можуть бути null");
        }

        try {
            if (entity.getUserId1().equals(userId)) {
                return entity.getUserId2();
            } else if (entity.getUserId2().equals(userId)) {
                return entity.getUserId1();
            } else {
                log.warn("Спроба отримати ID друга для користувача {}, який не є учасником дружби (учасники: {}, {})",
                        userId, entity.getUserId1(), entity.getUserId2());
                throw new IllegalArgumentException("Користувач не є стороною в цих відносинах дружби");
            }
        } catch (Exception e) {
            log.error("Помилка при отриманні ID друга: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Перетворює колекцію сутностей дружби в колекцію DTO з точки зору конкретного користувача.
     * <p>
     * Метод обробляє кожну сутність з вхідної колекції і створює відповідний DTO.
     * Якщо під час конвертації окремої сутності виникає помилка, вона логується,
     * але не перериває процес конвертації інших сутностей.
     * </p>
     *
     * @param entities колекція сутностей дружби для конвертації
     * @param userId   ID користувача, з точки зору якого робиться перетворення
     * @return колекція DTO дружби
     */
    public List<FriendshipDTO> toDtoList(List<FriendshipEntity> entities, Long userId) {
        if (entities == null) {
            log.debug("Спроба конвертації null списку дружб в DTO");
            return Collections.emptyList();
        }

        if (userId == null) {
            log.warn("Спроба конвертації списку дружб з null ID користувача");
            throw new IllegalArgumentException("ID користувача не може бути null");
        }

        try {
            List<FriendshipDTO> result = new ArrayList<>(entities.size());

            for (FriendshipEntity entity : entities) {
                try {
                    FriendshipDTO dto = toDto(entity, userId);
                    if (dto != null) {
                        result.add(dto);
                    }
                } catch (Exception e) {
                    log.warn("Пропускаємо конвертацію дружби через помилку: {}", e.getMessage());
                }
            }

            log.info("Успішно сконвертовано {} з {} сутностей дружби в DTO для користувача {}",
                    result.size(), entities.size(), userId);
            return result;
        } catch (Exception e) {
            log.error("Критична помилка при конвертації списку дружб в DTO: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}
