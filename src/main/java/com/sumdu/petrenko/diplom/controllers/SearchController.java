package com.sumdu.petrenko.diplom.controllers;

import com.sumdu.petrenko.diplom.dto.UserDTO;
import com.sumdu.petrenko.diplom.services.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контролер для обробки запитів, пов'язаних з пошуком користувачів.
 * <p>
 * Цей контролер надає API для пошуку користувачів за їхніми нікнеймами.
 * </p>
 */
@RestController
@RequestMapping("/search")
@Tag(name = "Search", description = "Operations related to searching users")
public class SearchController {
    /**
     * Сервіс для роботи з пошуком користувачів.
     */
    private final SearchService searchService;

    /**
     * Конструктор контролера пошуку.
     *
     * @param searchService сервіс для роботи з пошуком користувачів
     */
    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    /**
     * Отримати список користувачів за частковим збігом нікнейму.
     *
     * @param nickname частина нікнейму для пошуку
     * @return список користувачів, які відповідають запиту
     */
    @GetMapping("/users")
    @Operation(
            summary = "Search users by nickname",
            description = "Fetches a list of users whose nicknames contain the specified string.",
            tags = {"Search"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved users list",
                            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserDTO.class)))),
                    @ApiResponse(responseCode = "404", description = "Users not found", content = @Content)
            }
    )
    public ResponseEntity<List<UserDTO>> searchUsersByNicknameContaining(
            @Parameter(description = "Substring of the nickname to search for") @RequestParam String nickname) {
        List<UserDTO> users = searchService.searchUsersByNickname(nickname);

        if (users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return new ResponseEntity<>(users, HttpStatus.OK);
    }
}
