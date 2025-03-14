package com.sumdu.petrenko.diplom.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sumdu.petrenko.diplom.configs.SecurityConfig;
import com.sumdu.petrenko.diplom.models.User;
import com.sumdu.petrenko.diplom.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    static Stream<User> provideUsers() {
        return Stream.of(
                new User(1L, "validUser1", "valid1@example.com", "Password1", null, null, null, null),
                new User(2L, "invalidUser@", "invalid@example.com", "Password1", null, null, null, null),
                new User(3L, "validUser2", "invalidEmail", "Password1", null, null, null, null),
                new User(4L, "validUser3", "valid3@example.com", "password", null, null, null, null),
                new User(5L, "validUser4", "valid4@example.com", "Password1", null, null, null, null)
        );
    }

    static Stream<Arguments> provideUsersWithStatus(HttpStatus status) {
        return provideUsers().map(user -> {
            HttpStatus resultStatus = user.getNickname().matches("^[a-zA-Z0-9]+$")
                    && user.getEmail().contains("@")
                    && user.getPassword().matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*?&]+$")
                    ? status : HttpStatus.BAD_REQUEST;

            return Arguments.of(user, resultStatus);
        });
    }

    static Stream<Arguments> provideUsersWithCreateStatus() {
        return provideUsersWithStatus(HttpStatus.CREATED);
    }

    static Stream<Arguments> provideUsersWithUpdateStatus() {
        return provideUsersWithStatus(HttpStatus.OK);
    }

    static Stream<Arguments> provideUsersWithConflictStatus() {
        return provideUsersWithStatus(HttpStatus.CONFLICT);
    }

    static Stream<Arguments> provideUsersWithNotFoundStatus() {
        return provideUsersWithStatus(HttpStatus.NOT_FOUND);
    }

    @ParameterizedTest
    @MethodSource("provideUsersWithCreateStatus")
    void testCreateUser(User user, HttpStatus expectedStatus) throws Exception {
        when(userService.saveUser(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().is(expectedStatus.value()));
    }

    @ParameterizedTest
    @MethodSource("provideUsersWithConflictStatus")
    void testCreateUserConflict(User user, HttpStatus expectedStatus) throws Exception {
        if (expectedStatus == HttpStatus.CONFLICT) {
            when(userService.saveUser(any(User.class))).thenThrow(new DataIntegrityViolationException("Duplicate entry"));
        } else {
            when(userService.saveUser(any(User.class))).thenReturn(user);
        }

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().is(expectedStatus.value()));
    }

    @ParameterizedTest
    @MethodSource("provideUsersWithUpdateStatus")
    void testUpdateUser(User user, HttpStatus expectedStatus) throws Exception {
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));

        mockMvc.perform(put("/users/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().is(expectedStatus.value()));
    }

    @ParameterizedTest
    @MethodSource("provideUsersWithNotFoundStatus")
    void testUpdateUserNotFound(User user, HttpStatus expectedStatus) throws Exception {
        when(userService.getUserById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(put("/users/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().is(expectedStatus.value()));
    }
}