package com.sumdu.petrenko.diplom.controllers;

import com.sumdu.petrenko.diplom.dto.UserDTO;
import com.sumdu.petrenko.diplom.models.Friendship;
import com.sumdu.petrenko.diplom.services.FriendshipService;
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
import java.util.Optional;

/**
 * Контролер для обробки запитів, пов'язаних з дружбами.
 * <p>
 * Цей контролер надає API для створення, отримання та видалення дружб.
 * </p>
 */
@RestController
@RequestMapping("/friendships")
@Tag(name = "Friendships", description = "Operations related to friendships")
public class FriendshipController {
    /**
     * Сервіс для роботи з дружбами.
     */
    private final FriendshipService friendshipService;

    /**
     * Конструктор контролера дружб.
     *
     * @param friendshipService сервіс для роботи з дружбами
     */
    @Autowired
    public FriendshipController(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    /**
     * Отримати запис про дружбу за його id.
     *
     * @param id ід користувача, для якого потрібно отримати дружби
     * @return список дружб
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Get friendship by ID",
            description = "Fetches a friendship by its unique ID.",
            tags = {"Retrieve"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved the friendship",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Friendship.class))),
                    @ApiResponse(responseCode = "404", description = "Friendship not found", content = @Content)
            }
    )
    public ResponseEntity<Friendship> getFriendshipById(
            @Parameter(description = "ID of the friendship to be fetched") @PathVariable Long id) {
        Optional<Friendship> friendship = friendshipService.getFriendshipById(id);

        return friendship.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Отримати список друзів для конкретного користувача.
     *
     * @param userId ід користувача, для якого потрібно отримати друзів
     * @return список друзів
     */
    @GetMapping("/user/{userId}")
    @Operation(
            summary = "Get friends of user",
            description = "Fetches a list of friends for a specific user by their unique ID.",
            tags = {"Retrieve"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved friends list",
                            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserDTO.class)))),
                    @ApiResponse(responseCode = "404", description = "Friends not found", content = @Content)
            }
    )
    public ResponseEntity<List<UserDTO>> getFriendsOfUser(
            @Parameter(description = "ID of the user whose friends are to be fetched") @PathVariable Long userId) {
        List<UserDTO> friends = friendshipService.getFriendsOfUser(userId);

        if (friends.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(friends, HttpStatus.OK);
    }

    /**
     * Створити нову дружбу.
     *
     * @param friendship об'єкт дружби, що містить деталі дружби
     * @return список дружб
     */
    @PostMapping
    @Operation(
            summary = "Create a new friendship",
            description = "Creates a new friendship with the provided details.",
            tags = {"Create"},
            responses = {
                    @ApiResponse(responseCode = "201", description = "Friendship created successfully", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Invalid friendship data", content = @Content)
            }
    )
    public ResponseEntity<Void> createFriendship(@RequestBody Friendship friendship) {
        friendshipService.saveFriendship(friendship);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Видалити дружбу за її id.
     *
     * @param id ід дружби, яку потрібно видалити
     * @return статус видалення
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a friendship by ID",
            description = "Deletes a friendship with the specified ID.",
            tags = {"Delete"},
            responses = {
                    @ApiResponse(responseCode = "204", description = "Friendship successfully deleted", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Friendship not found", content = @Content)
            }
    )
    public ResponseEntity<Void> deleteFriendship(
            @Parameter(description = "ID of the friendship to be deleted") @PathVariable Long id) {
        Optional<Friendship> friendshipOptional = friendshipService.getFriendshipById(id);

        if (friendshipOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        friendshipService.deleteFriendship(id);
        return ResponseEntity.noContent().build();
    }
}
