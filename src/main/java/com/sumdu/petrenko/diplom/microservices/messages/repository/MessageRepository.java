package com.sumdu.petrenko.diplom.microservices.messages.repository;

import com.sumdu.petrenko.diplom.microservices.messages.models.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
    /**
     * Пошук останніх повідомлень для конкретного чату з обмеженням кількості.
     *
     * @param chatId ідентифікатор чату
     * @param limit  кількість повідомлень для отримання
     * @return список повідомлень, відсортованих за часом створення (спочатку найновіші)
     */
    @Query(value = "SELECT m FROM MessageEntity m WHERE m.chat.id = :chatId ORDER BY m.creationTime DESC LIMIT :limit")
    List<MessageEntity> findByChatIdOrderByCreationTimeDesc(
            @Param("chatId") Long chatId,
            @Param("limit") int limit);

    /**
     * Пошук повідомлень для конкретного чату з ідентифікатором більшим за вказаний.
     *
     * @param chatId    ідентифікатор чату
     * @param messageId ідентифікатор повідомлення, після якого потрібно отримати нові повідомлення
     * @param limit     кількість повідомлень для отримання
     * @return список повідомлень, відсортованих за часом створення
     */
    @Query("SELECT m FROM MessageEntity m WHERE m.chat.id = :chatId AND m.messageId > :messageId ORDER BY m.creationTime ASC LIMIT :limit")
    List<MessageEntity> findByChatIdAndMessageIdGreaterThanOrderByCreationTimeAsc(
            @Param("chatId") Long chatId,
            @Param("messageId") Long messageId,
            @Param("limit") int limit);

    /**
     * Пошук повідомлень для конкретного чату з ідентифікатором більшим за вказаний.
     *
     * @param chatId    ідентифікатор чату
     * @param messageId ідентифікатор повідомлення, до якого потрібно отримати нові повідомлення
     * @param limit     кількість повідомлень для отримання
     * @return список повідомлень, відсортованих за часом створення
     */
    @Query("SELECT m FROM MessageEntity m WHERE m.chat.id = :chatId AND m.messageId < :messageId ORDER BY m.creationTime ASC LIMIT :limit")
    List<MessageEntity> findByChatIdAndMessageIdLowerThanOrderByCreationTimeAsc(
            @Param("chatId") Long chatId,
            @Param("messageId") Long messageId,
            @Param("limit") int limit);

    /**
     * Підрахунок кількості повідомлень у конкретному чаті.
     *
     * @param chatId ідентифікатор чату
     * @return кількість повідомлень
     */
    long countByChatId(Long chatId);

    /**
     * Пошук повідомлень за текстом у конкретному чаті.
     *
     * @param chatId ідентифікатор чату
     * @param text   текст для пошуку
     * @return список повідомлень, що містять шуканий текст
     */
    List<MessageEntity> findByChatIdAndMessageTextContainingIgnoreCase(Long chatId, String text);

    /**
     * Отримання повідомлень за проміжок часу.
     *
     * @param chatId    ідентифікатор чату
     * @param startTime початковий час
     * @param endTime   кінцевий час
     * @return список повідомлень за вказаний проміжок часу
     */
    List<MessageEntity> findByChatIdAndCreationTimeBetween(Long chatId, LocalDateTime startTime, LocalDateTime endTime);
}

