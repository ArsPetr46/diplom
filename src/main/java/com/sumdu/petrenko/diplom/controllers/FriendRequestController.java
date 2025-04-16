package com.sumdu.petrenko.diplom.controllers;

import com.sumdu.petrenko.diplom.models.FriendRequest;
import com.sumdu.petrenko.diplom.services.FriendRequestService;
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
 * Контролер для обробки запитів, пов'язаних з дружніми запитами.
 * <p>
 * Цей контролер надає API для створення, отримання та видалення дружніх запитів.
 * </p>
 */
@RestController
@RequestMapping("/friendrequests")
@Tag(name = "FriendRequests", description = "Operations related to friend requests")
public class FriendRequestController {
    /**
     * Сервіс для роботи з дружніми запитами.
     */
    private final FriendRequestService friendRequestService;

    /**
     * Конструктор контролера дружніх запитів.
     *
     * @param friendRequestService сервіс для роботи з дружніми запитами
     */
    @Autowired
    public FriendRequestController(FriendRequestService friendRequestService) {
        this.friendRequestService = friendRequestService;
    }

    /**
     * Отримати список всіх дружніх запитів пов'язаних з певним id.
     *
     * @param id ід користувача, для якого потрібно отримати дружні запити
     * @return список дружніх запитів
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Get friend request by ID",
            description = "Fetches a friend request by its unique ID.",
            tags = {"Retrieve"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved the friend request",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = FriendRequest.class))),
                    @ApiResponse(responseCode = "404", description = "Friend request not found", content = @Content)
            }
    )
    public ResponseEntity<FriendRequest> getFriendRequestById(
            @Parameter(description = "ID of the friend request to be fetched") @PathVariable Long id) {
        Optional<FriendRequest> friendRequest = friendRequestService.getFriendRequestById(id);

        return friendRequest.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Отримати список всіх дружніх запитів, де користувач з певним id є відправником.
     *
     * @param senderId ід користувача, який надіслав дружній запит
     * @return список дружніх запитів
     */
    @GetMapping("/sender/{senderId}")
    @Operation(
            summary = "Get friend requests by sender ID",
            description = "Fetches a list of friend requests sent by a specific user.",
            tags = {"Retrieve"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved friend requests list",
                            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = FriendRequest.class)))),
                    @ApiResponse(responseCode = "404", description = "Friend requests not found", content = @Content)
            }
    )
    public ResponseEntity<List<FriendRequest>> getFriendRequestsBySenderId(
            @Parameter(description = "ID of the sender whose friend requests are to be fetched") @PathVariable Long senderId) {
        List<FriendRequest> friendRequests = friendRequestService.getFriendRequestsBySenderId(senderId);

        if (friendRequests.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(friendRequests, HttpStatus.OK);
    }

    /**
     * Отримати список всіх дружніх запитів, де користувач з певним id є отримувачем.
     *
     * @param receiverId ід користувача, який отримав дружній запит
     * @return список дружніх запитів
     */
    @GetMapping("/receiver/{receiverId}")
    @Operation(
            summary = "Get friend requests by receiver ID",
            description = "Fetches a list of friend requests received by a specific user.",
            tags = {"Retrieve"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved friend requests list",
                            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = FriendRequest.class)))),
                    @ApiResponse(responseCode = "404", description = "Friend requests not found", content = @Content)
            }
    )
    public ResponseEntity<List<FriendRequest>> getFriendRequestsByReceiverId(
            @Parameter(description = "ID of the receiver whose friend requests are to be fetched") @PathVariable Long receiverId) {
        List<FriendRequest> friendRequests = friendRequestService.getFriendRequestsByReceiverId(receiverId);

        if (friendRequests.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(friendRequests, HttpStatus.OK);
    }

    /**
     * Створити новий дружній запит.
     *
     * @param friendRequest об'єкт дружнього запиту, що містить дані для створення нового запиту
     * @return список дружніх запитів
     */
    @PostMapping
    @Operation(
            summary = "Create a new friend request",
            description = "Creates a new friend request with the provided details.",
            tags = {"Create"},
            responses = {
                    @ApiResponse(responseCode = "201", description = "Friend request created successfully", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Invalid friend request data", content = @Content)
            }
    )
    public ResponseEntity<Void> createFriendRequest(@RequestBody FriendRequest friendRequest) {
        friendRequestService.saveFriendRequest(friendRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Видалити дружній запит за ID.
     *
     * @param id ід дружнього запиту, який потрібно видалити
     * @return статус видалення
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a friend request by ID",
            description = "Deletes a friend request with the specified ID.",
            tags = {"Delete"},
            responses = {
                    @ApiResponse(responseCode = "204", description = "Friend request successfully deleted", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Friend request not found", content = @Content)
            }
    )
    public ResponseEntity<Void> deleteFriendRequest(
            @Parameter(description = "ID of the friend request to be deleted") @PathVariable Long id) {
        Optional<FriendRequest> friendRequestOptional = friendRequestService.getFriendRequestById(id);

        if (friendRequestOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        friendRequestService.deleteFriendRequest(id);
        return ResponseEntity.noContent().build();
    }
}
