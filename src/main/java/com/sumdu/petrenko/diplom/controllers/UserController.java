package com.sumdu.petrenko.diplom.controllers;

import com.sumdu.petrenko.diplom.dto.UserDTO;
import com.sumdu.petrenko.diplom.models.User;
import com.sumdu.petrenko.diplom.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.links.Link;
import io.swagger.v3.oas.annotations.links.LinkParameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Контролер для обробки запитів, пов'язаних з користувачами.
 * <p>
 * Цей контролер надає API для створення, отримання, оновлення та видалення користувачів.
 * </p>
 */
@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "Operations related to users")
public class UserController {
    /**
     * Сервіс для роботи з користувачами.
     */
    private final UserService userService;

    /**
     * Конструктор контролера користувачів.
     *
     * @param userService сервіс для роботи з користувачами
     */
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Отримати список всіх користувачів.
     *
     * @return список користувачів
     */
    @GetMapping
    @Operation(
            summary = "Get all users",
            description = "Fetches a list of all users in DB.",
            tags = {"Retrieve"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved users list",
                            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserDTO.class)))),
                    @ApiResponse(responseCode = "404", description = "Users not found", content = @Content)
            }
    )
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        Optional<List<UserDTO>> usersListOptional = userService.getAllUsersAsDTO();

        return usersListOptional.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());

    }

    /**
     * Отримати користувача за його id.
     *
     * @param id ід користувача, для якого потрібно отримати дані
     * @return дані користувача
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Get user by ID",
            description = "Fetches a user by their unique ID.",
            tags = {"Retrieve"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved the user",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class)),
                            links = {
                                    @Link(name = "userFriends", operationId = "getFriendsOfUser", parameters = @LinkParameter(name = "userId", expression = "$response.body#/id"))
                            }),
                    @ApiResponse(responseCode = "400", description = "Invalid user ID", content = @Content),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
            }
    )
    public ResponseEntity<UserDTO> getUserById(@Parameter(description = "ID of the user to be fetched") @PathVariable Long id) {
        Optional<UserDTO> userOptional = userService.getUserByIdAsDTO(id);

        return userOptional.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());

    }

    /**
     * Створити нового користувача.
     *
     * @param user об'єкт користувача, що містить деталі нового користувача
     * @return дані користувача
     */
    @PostMapping(consumes = "application/json", produces = "application/json")
    @Operation(
            summary = "Create a new user",
            description = "Creates a new user with the provided details.",
            tags = {"Create"},
            responses = {
                    @ApiResponse(responseCode = "201", description = "User created successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid user data", content = @Content)
            }
    )
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.saveUser(user));
    }

    /**
     * Оновити існуючого користувача.
     *
     * @param id         ід користувача, якого потрібно оновити
     * @param userDetails об'єкт користувача з новими даними
     * @return статус оновлення
     */
    @PutMapping("/{id}")
    @Operation(
            summary = "Update an existing user",
            description = "Updates the details of an existing user by ID. Username and Email will not be changed.",
            tags = {"Update"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "User updated successfully", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Invalid user data", content = @Content),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
            }
    )
    public ResponseEntity<Void> updateUser(@PathVariable Long id, @Valid @RequestBody User userDetails) {
        Optional<User> userOptional = userService.getUserById(id);

        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();

            User updatedUser = new User(
                    existingUser.getId(),
                    userDetails.getNickname(),
                    existingUser.getEmail(),
                    userDetails.getPassword(),
                    userDetails.getUserDescription(),
                    userDetails.getBirthDate(),
                    userDetails.getAvatarUrl(),
                    existingUser.getUserCreationTime()
            );

            userService.saveUser(updatedUser);

            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Видалити користувача за його id.
     *
     * @param id ід користувача, якого потрібно видалити
     * @return статус видалення
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a user by ID",
            description = "Deletes a user with the specified ID.",
            tags = {"Delete"},
            responses = {
                    @ApiResponse(responseCode = "204", description = "User successfully deleted", content = @Content),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
            }
    )
    public ResponseEntity<Void> deleteUser(@Parameter(description = "ID of the user to be deleted") @PathVariable Long id) {
        Optional<UserDTO> userOptional = userService.getUserByIdAsDTO(id);

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
