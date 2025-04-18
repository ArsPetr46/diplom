package com.sumdu.petrenko.diplom.controllers.advices;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

/**
 * Глобальний обробник виключень для контролерів.
 * <p>
 * Цей клас обробляє виключення, що виникають у контролерах, і повертає відповідь з відповідним статусом
 * та повідомленням про помилку.
 * </p>
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Логер для глобального обробника виключень.
     */
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Обробляє виключення DataIntegrityViolationException.
     *
     * @param ex виключення, що виникло
     * @return відповідь з кодом статусу 409 (CONFLICT) та повідомленням про помилку
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        logger.error("Порушення цілісності даних: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.CONFLICT).body("Порушення цілісності даних: " + ex.getMessage());
    }

    /**
     * Обробляє виключення MethodArgumentTypeMismatchException.
     *
     * @param ex виключення, що виникло
     * @return відповідь з кодом статусу 400 (BAD REQUEST) та повідомленням про помилку
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        logger.error("Помилка конвертації параметра: {}", ex.getMessage(), ex);

        Map<String, String> error = new HashMap<>();
        error.put("error", "Некоректний формат параметра: " + ex.getName());
        error.put("value", String.valueOf(ex.getValue()));
        error.put("required_type", ex.getRequiredType().getSimpleName());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Обробляє виключення IllegalArgumentException.
     *
     * @param ex виключення, що виникло
     * @return відповідь з кодом статусу 400 (BAD REQUEST) та повідомленням про помилку
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        logger.error("Некоректний аргумент: {}", ex.getMessage(), ex);

        Map<String, String> error = new HashMap<>();
        error.put("error", "Некоректний аргумент: " + ex.getMessage());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Обробляє виключення ConstraintViolationException.
     *
     * @param ex виключення, що виникло
     * @return відповідь з кодом статусу 400 (BAD REQUEST) та повідомленням про помилку
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolation(ConstraintViolationException ex) {
        logger.error("Порушення обмеження: {}", ex.getMessage(), ex);

        Map<String, String> error = new HashMap<>();
        error.put("error", "Порушення обмеження валідації: " + ex.getMessage());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Обробляє всі інші виключення.
     *
     * @param ex виключення, що виникло
     * @return відповідь з кодом статусу 500 (INTERNAL SERVER ERROR) та повідомленням про помилку
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        logger.error("Непередбачена помилка: {}", ex.getMessage(), ex);

        Map<String, String> error = new HashMap<>();
        error.put("error", "Внутрішня помилка сервера");

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
