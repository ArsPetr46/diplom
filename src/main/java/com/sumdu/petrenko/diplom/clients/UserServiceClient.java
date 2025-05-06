package com.sumdu.petrenko.diplom.clients;

import com.sumdu.petrenko.diplom.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

/**
 * Клієнт для взаємодії з мікросервісом користувачів.
 * <p>
 * Цей клас надає методи для виконання HTTP-запитів до API сервісу користувачів.
 * Він інкапсулює логіку взаємодії між мікросервісами та обробляє помилки, що можуть
 * виникнути під час комунікації.
 * </p>
 * <p>
 * Клієнт використовує WebClient для асинхронної взаємодії та підтримує таймаути і ретраї
 * для забезпечення стійкості до тимчасових мережевих проблем.
 * </p>
 */
@Component
@Slf4j
public class UserServiceClient {
    /**
     * WebClient для виконання HTTP-запитів.
     * <p>
     * Використовується для взаємодії з API сервісу користувачів.
     * </p>
     */
    private final WebClient webClient;

    /**
     * URL мікросервісу користувачів.
     * Може бути змінений через конфігурацію.
     */
    @Value("${microservices.user-service.url}")
    private String userServiceUrl;

    /**
     * Таймаут для запитів до мікросервісу користувачів (в секундах).
     */
    private static final int REQUEST_TIMEOUT_SECONDS = 5;

    /**
     * Максимальна кількість спроб повторення запиту при тимчасових помилках.
     */
    private static final int MAX_RETRY_ATTEMPTS = 3;

    /**
     * Конструктор клієнта мікросервісу користувачів.
     * <p>
     * Створює налаштований екземпляр WebClient для здійснення HTTP-запитів
     * до API мікросервісу користувачів.
     * </p>
     *
     * @param webClientBuilder будівельник WebClient для створення налаштованого клієнта
     */
    public UserServiceClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl(userServiceUrl)
                .build();
    }

    /**
     * Отримує дані користувача за його ідентифікатором.
     * <p>
     * Метод виконує GET-запит до API мікросервісу користувачів для отримання даних
     * користувача з вказаним ID. Обробляє різні типи виключень, що можуть виникнути
     * під час запиту, включаючи помилки мережі та відсутність користувача.
     * </p>
     * <p>
     * У разі тимчасових мережевих проблем запит автоматично повторюється
     * до {@link #MAX_RETRY_ATTEMPTS} разів з експоненціальною затримкою.
     * </p>
     *
     * @param userId ідентифікатор користувача
     * @return ResponseEntity з даними користувача або з кодом помилки
     */
    public ResponseEntity<UserDTO> getUserById(Long userId) {
        if (userId == null) {
            log.warn("Спроба отримати користувача з null ID");
            return ResponseEntity.badRequest().build();
        }

        log.info("Запит до user-service для отримання користувача з ID: {}", userId);
        try {
            UserDTO userDTO = webClient.get()
                    .uri("/api/users/{id}", userId)
                    .retrieve()
                    .bodyToMono(UserDTO.class)
                    .timeout(Duration.ofSeconds(REQUEST_TIMEOUT_SECONDS))
                    .retryWhen(Retry.backoff(MAX_RETRY_ATTEMPTS, Duration.ofMillis(500))
                            .filter(e -> e instanceof WebClientException && !(e instanceof WebClientResponseException)))
                    .onErrorResume(WebClientResponseException.class, e -> {
                        if (e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                            log.info("Користувача з ID {} не знайдено", userId);
                            return Mono.empty();
                        }
                        log.error("Помилка відповіді при отриманні користувача з ID {}: HTTP статус {}, {}",
                                userId, e.getStatusCode(), e.getMessage());
                        return Mono.error(e);
                    })
                    .onErrorResume(WebClientException.class, e -> {
                        log.error("Помилка мережі при отриманні користувача з ID {}: {}", userId, e.getMessage());
                        return Mono.error(e);
                    })
                    .onErrorResume(e -> {
                        log.error("Непередбачена помилка при отриманні користувача з ID {}: {}", userId, e.getMessage(), e);
                        return Mono.empty();
                    })
                    .block();

            return userDTO != null
                    ? ResponseEntity.ok(userDTO)
                    : ResponseEntity.notFound().build();
        } catch (WebClientResponseException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (WebClientException e) {
            log.error("Критична помилка мережі при зверненні до user-service: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        } catch (Exception e) {
            log.error("Критична помилка при зверненні до user-service: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Перевіряє, чи існує користувач з вказаним ідентифікатором.
     * <p>
     * Метод виконує GET-запит до API мікросервісу користувачів для перевірки наявності
     * користувача з вказаним ID. Обробляє різні типи виключень, що можуть виникнути
     * під час запиту.
     * </p>
     * <p>
     * На відміну від методу {@link #getUserById}, цей метод не завантажує повні дані
     * користувача, а лише перевіряє його існування, що оптимізує використання мережі.
     * </p>
     *
     * @param userId ідентифікатор користувача для перевірки
     * @return ResponseEntity з результатом перевірки або з кодом помилки
     */
    public ResponseEntity<Boolean> existsById(Long userId) {
        if (userId == null) {
            log.warn("Спроба перевірити існування користувача з null ID");
            return ResponseEntity.badRequest().build();
        }

        log.info("Перевірка існування користувача з ID: {}", userId);
        try {
            Boolean exists = webClient.get()
                    .uri("/api/users/{id}/exists", userId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .timeout(Duration.ofSeconds(REQUEST_TIMEOUT_SECONDS))
                    .retryWhen(Retry.backoff(MAX_RETRY_ATTEMPTS, Duration.ofMillis(500))
                            .filter(e -> e instanceof WebClientException && !(e instanceof WebClientResponseException)))
                    .onErrorReturn(false)
                    .block();

            boolean result = exists != null && exists;
            log.info("Результат перевірки існування користувача з ID {}: {}", userId, result);
            return ResponseEntity.ok(result);
        } catch (WebClientResponseException e) {
            log.error("Помилка відповіді при перевірці існування користувача {}: HTTP статус {}, {}",
                    userId, e.getStatusCode(), e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (WebClientException e) {
            log.error("Помилка мережі при перевірці існування користувача {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        } catch (Exception e) {
            log.error("Непередбачена помилка при перевірці існування користувача {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Отримує список користувачів за переліком їх ідентифікаторів.
     * <p>
     * Метод виконує POST-запит до API мікросервісу користувачів для отримання даних
     * кількох користувачів за один запит. Оптимізує мережеву взаємодію при необхідності
     * отримати дані багатьох користувачів.
     * </p>
     *
     * @param userIds список ідентифікаторів користувачів
     * @return ResponseEntity зі списком даних користувачів або з кодом помилки
     */
    public ResponseEntity<List<UserDTO>> getUsersByIds(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            log.warn("Спроба отримати користувачів з порожнім списком ID");
            return ResponseEntity.ok(Collections.emptyList());
        }

        log.debug("Запит до user-service для отримання {} користувачів", userIds.size());
        try {
            List<UserDTO> userDTOs = webClient.post()
                    .uri("/api/users/batch")
                    .bodyValue(userIds)
                    .retrieve()
                    .bodyToFlux(UserDTO.class)
                    .timeout(Duration.ofSeconds(REQUEST_TIMEOUT_SECONDS * 2))
                    .collectList()
                    .block();

            if (userDTOs != null) {
                log.info("Отримано {} користувачів з {} запитаних", userDTOs.size(), userIds.size());
                return ResponseEntity.ok(userDTOs);
            } else {
                return ResponseEntity.ok(Collections.emptyList());
            }
        } catch (WebClientResponseException e) {
            log.error("Помилка відповіді при отриманні користувачів: HTTP статус {}, {}",
                    e.getStatusCode(), e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (WebClientException e) {
            log.error("Помилка мережі при отриманні користувачів: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        } catch (Exception e) {
            log.error("Непередбачена помилка при отриманні користувачів: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
