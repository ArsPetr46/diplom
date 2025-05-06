package com.sumdu.petrenko.diplom.clients;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;

/**
 * Клієнт для взаємодії з мікросервісом чатів.
 * <p>
 * Цей клас надає методи для виконання HTTP-запитів до API сервісу чатів.
 * Він використовує WebClient для асинхронної взаємодії між мікросервісами.
 * </p>
 * <p>
 * Клієнт обробляє помилки мережі та інші виключення, що можуть виникнути під час комунікації,
 * і надає уніфікований інтерфейс для роботи з чатами з інших компонентів системи.
 * </p>
 */
@Component
@Slf4j
public class ChatServiceClient {
    /**
     * WebClient для виконання HTTP-запитів.
     * <p>
     * Використовується для взаємодії з API сервісу повідомлень.
     * </p>
     */
    private final WebClient webClient;

    /**
     * URL мікросервісу чатів.
     * Може бути змінений через конфігурацію.
     */
    @Value("${microservices.chat-service.url}")
    private String chatServiceUrl;

    /**
     * Таймаут для запитів до мікросервісу чатів (в секундах).
     */
    private static final int REQUEST_TIMEOUT_SECONDS = 5;

    /**
     * Конструктор клієнта мікросервісу чатів.
     * <p>
     * Створює налаштований екземпляр WebClient для здійснення HTTP-запитів
     * до API мікросервісу чатів.
     * </p>
     *
     * @param webClientBuilder будівельник WebClient для створення налаштованого клієнта
     */
    public ChatServiceClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl(chatServiceUrl)
                .build();
    }

    /**
     * Перевіряє, чи існує чат з вказаним ID.
     * <p>
     * Метод виконує GET-запит до API мікросервісу чатів для перевірки наявності чату
     * з вказаним ідентифікатором. Обробляє різні типи виключень, що можуть виникнути
     * під час запиту, включаючи помилки мережі та помилки відповіді.
     * </p>
     *
     * @param chatId ID чату для перевірки
     * @return ResponseEntity з результатом перевірки або з кодом помилки
     */
    public ResponseEntity<Boolean> existsById(Long chatId) {
        if (chatId == null) {
            log.warn("Спроба перевірити існування чату з null ID");
            return ResponseEntity.badRequest().build();
        }

        log.debug("Перевірка існування чату з ID: {}", chatId);
        try {
            Boolean exists = webClient.get()
                    .uri("/api/chats/{id}/exists", chatId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .timeout(Duration.ofSeconds(REQUEST_TIMEOUT_SECONDS))
                    .onErrorReturn(false)
                    .block();

            boolean result = exists != null && exists;
            log.info("Результат перевірки існування чату з ID {}: {}", chatId, result);
            return ResponseEntity.ok(result);
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                log.info("Чат з ID {} не знайдено", chatId);
                return ResponseEntity.ok(false);
            }
            log.error("Помилка відповіді при перевірці існування чату {}: HTTP статус {}, {}",
                    chatId, e.getStatusCode(), e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (WebClientException e) {
            log.error("Помилка мережі при перевірці існування чату {}: {}", chatId, e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        } catch (Exception e) {
            log.error("Непередбачена помилка при перевірці існування чату {}: {}", chatId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Створює новий чат.
     * <p>
     * Метод виконує POST-запит до API мікросервісу чатів для створення нового чату.
     * Обробляє помилки мережі та інші виключення, які можуть виникнути під час запиту.
     * </p>
     *
     * @return ResponseEntity з ID створеного чату або з кодом помилки
     */
    public ResponseEntity<Long> createChat() {
        log.debug("Створення нового чату через ChatServiceClient");
        try {
            Long chatId = webClient.post()
                    .uri("/api/chats")
                    .retrieve()
                    .bodyToMono(Long.class)
                    .timeout(Duration.ofSeconds(REQUEST_TIMEOUT_SECONDS))
                    .block();

            log.info("Створено новий чат з ID: {}", chatId);
            return ResponseEntity.ok(chatId);
        } catch (WebClientResponseException e) {
            HttpStatusCode status = e.getStatusCode();
            log.error("Помилка відповіді при створенні чату: HTTP статус {}, {}", status, e.getMessage());
            return ResponseEntity.status(status).build();
        } catch (WebClientException e) {
            log.error("Помилка мережі при створенні чату: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        } catch (Exception e) {
            log.error("Непередбачена помилка при створенні чату: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
