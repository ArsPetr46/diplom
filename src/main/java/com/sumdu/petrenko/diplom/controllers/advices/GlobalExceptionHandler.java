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
     * Коди помилок.
     */
    private static final String DATA_INTEGRITY_ERROR = "ERR-1001";
    private static final String TYPE_MISMATCH_ERROR = "ERR-1002";
    private static final String ILLEGAL_ARGUMENT_ERROR = "ERR-1003";
    private static final String CONSTRAINT_VIOLATION_ERROR = "ERR-1004";
    private static final String GENERAL_ERROR = "ERR-5000";

    /**
     * Обробляє виключення DataIntegrityViolationException.
     *
     * @param ex виключення, що виникло
     * @return відповідь з кодом статусу 409 (CONFLICT) та повідомленням про помилку
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        String errorMessage = "Порушення цілісності даних: " + ex.getMessage();
        logger.error("{}: {}", DATA_INTEGRITY_ERROR, errorMessage, ex);

        Map<String, String> error = new HashMap<>();
        error.put("errorCode", DATA_INTEGRITY_ERROR);
        error.put("error", errorMessage);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Обробляє виключення MethodArgumentTypeMismatchException.
     *
     * @param ex виключення, що виникло
     * @return відповідь з кодом статусу 400 (BAD REQUEST) та повідомленням про помилку
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String errorMessage = "Помилка конвертації параметра: " + ex.getName();
        logger.error("{}: {}", TYPE_MISMATCH_ERROR, errorMessage, ex);

        Map<String, String> error = new HashMap<>();
        error.put("errorCode", TYPE_MISMATCH_ERROR);
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
        String errorMessage = "Некоректний аргумент: " + ex.getMessage();
        logger.error("{}: {}", ILLEGAL_ARGUMENT_ERROR, errorMessage, ex);

        Map<String, String> error = new HashMap<>();
        error.put("errorCode", ILLEGAL_ARGUMENT_ERROR);
        error.put("error", errorMessage);

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
        String errorMessage = "Порушення обмеження: " + ex.getMessage();
        logger.error("{}: {}", CONSTRAINT_VIOLATION_ERROR, errorMessage, ex);

        Map<String, String> error = new HashMap<>();
        error.put("errorCode", CONSTRAINT_VIOLATION_ERROR);
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
        String errorId = GENERAL_ERROR + "-" + System.currentTimeMillis();
        logger.error("{}: Непередбачена помилка: {}", errorId, ex.getMessage(), ex);

        Map<String, String> error = new HashMap<>();
        error.put("errorCode", errorId);
        error.put("error", "Внутрішня помилка сервера");

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
