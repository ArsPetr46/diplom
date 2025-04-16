package com.sumdu.petrenko.diplom.controllers.advices;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

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
     * Обробляє виключення DataIntegrityViolationException.
     *
     * @param ex виключення, що виникло
     * @return відповідь з кодом статусу 409 (CONFLICT) та повідомленням про помилку
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Data integrity violation: " + ex.getMessage());
    }
}
