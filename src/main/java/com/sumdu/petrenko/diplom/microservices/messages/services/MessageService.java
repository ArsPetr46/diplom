package com.sumdu.petrenko.diplom.microservices.messages.services;

import com.sumdu.petrenko.diplom.microservices.messages.models.MessageEntity;
import com.sumdu.petrenko.diplom.microservices.messages.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Сервіс для роботи з повідомленнями.
 * <p>
 * Цей сервіс надає методи для створення, читання, оновлення та видалення повідомлень,
 * а також для роботи з повідомленнями в чатах.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final MessageRepository messageRepository;

    /**
     * Отримання повідомлення за ідентифікатором.
     *
     * @param messageId ідентифікатор повідомлення
     * @return опціональне значення, що містить повідомлення, якщо воно знайдено
     */
    public Optional<MessageEntity> getMessageById(Long messageId) {
        return messageRepository.findById(messageId);
    }

    /**
     * Отримання останніх повідомлень для конкретного чату.
     *
     * @param chatId ідентифікатор чату
     * @param limit  кількість повідомлень для отримання
     * @return список повідомлень
     */
    public List<MessageEntity> getLatestChatMessages(Long chatId, int limit) {
        return messageRepository.findByChatIdOrderByCreationTimeDesc(chatId, limit);
    }

    /**
     * Отримання останніх повідомлень для конкретного чату після певного ідентифікатора повідомлення.
     *
     * @param chatId    ідентифікатор чату
     * @param limit     кількість повідомлень для отримання
     * @param messageId ідентифікатор повідомлення, після якого потрібно отримати нові повідомлення
     * @return список повідомлень
     */
    public List<MessageEntity> getLatestChatMessagesAfterId(Long chatId, int limit, Long messageId) {
        return messageRepository.findByChatIdAndMessageIdGreaterThanOrderByCreationTimeAsc(
                chatId, messageId, limit);
    }

    /**
     * Отримання останніх повідомлень для конкретного чату до певного ідентифікатора повідомлення.
     *
     * @param chatId    ідентифікатор чату
     * @param limit     кількість повідомлень для отримання
     * @param messageId ідентифікатор повідомлення, до якого потрібно отримати нові повідомлення
     * @return список повідомлень
     */
    public List<MessageEntity> getLatestChatMessagesBeforeId(Long chatId, int limit, Long messageId) {
        return messageRepository.findByChatIdAndMessageIdLowerThanOrderByCreationTimeAsc(
                chatId, messageId, limit);
    }

    /**
     * Створення нового повідомлення.
     *
     * @param messageEntity дані повідомлення
     * @return створене повідомлення
     */
    public MessageEntity createMessage(MessageEntity messageEntity) {
        return messageRepository.save(messageEntity);
    }

    /**
     * Оновлення існуючого повідомлення.
     *
     * @param messageId     ідентифікатор повідомлення
     * @param messageEntity нові дані повідомлення
     * @return опціональне значення, що містить оновлене повідомлення, якщо воно знайдено
     */
    @Transactional
    public Optional<MessageEntity> updateMessage(Long messageId, MessageEntity messageEntity) {
        return messageRepository.findById(messageId)
                .map(existingMessage -> {
                    if (messageEntity.getMessageText() != null) {
                        existingMessage.setMessageText(messageEntity.getMessageText());
                    }
                    if (messageEntity.getMediaURL() != null) {
                        existingMessage.setMediaURL(messageEntity.getMediaURL());
                    }

                    return messageRepository.save(existingMessage);
                });
    }

    /**
     * Видалення повідомлення.
     *
     * @param messageId ідентифікатор повідомлення
     * @return true, якщо повідомлення успішно видалено, інакше false
     */
    public boolean deleteMessage(Long messageId) {
        return messageRepository.findById(messageId)
                .map(message -> {
                    messageRepository.delete(message);
                    return true;
                })
                .orElse(false);
    }
}

