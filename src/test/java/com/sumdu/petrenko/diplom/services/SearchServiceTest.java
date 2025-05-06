package com.sumdu.petrenko.diplom.services;

import com.sumdu.petrenko.diplom.dto.UserDTO;
import com.sumdu.petrenko.diplom.microservices.searchservice.services.SearchService;
import com.sumdu.petrenko.diplom.microservices.users.models.UserEntity;
import com.sumdu.petrenko.diplom.microservices.users.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class SearchServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SearchService searchService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSearchUsersByMultipleCriteria() {
        String nickname = "john";
        String email = "john@example.com";

        UserEntity userEntity = new UserEntity(
                nickname,
                email,
                "Password1"
        );

        when(userRepository.findAll()).thenReturn(Arrays.asList(userEntity));

        List<UserDTO> users = searchService.searchUsersByMultipleCriteria(nickname, email);

        assertEquals(1, users.size());
        assertEquals(nickname, users.get(0).getNickname());
        assertEquals(email, users.get(0).getEmail());
    }

    @Test
    public void testSearchUsersByMultipleCriteria_NicknameOnly() {
        String nickname = "john";

        UserEntity userEntity = new UserEntity(
                nickname,
                "john@example.com",
                "Password1"
        );

        when(userRepository.findAll()).thenReturn(Arrays.asList(userEntity));

        List<UserDTO> users = searchService.searchUsersByMultipleCriteria(nickname, null);

        assertEquals(1, users.size());
        assertEquals(nickname, users.get(0).getNickname());
    }

    @Test
    public void testSearchUsersByMultipleCriteria_EmailOnly() {
        String email = "john@example.com";

        UserEntity userEntity = new UserEntity(
                "john",
                email,
                "Password1"
        );

        when(userRepository.findAll()).thenReturn(Arrays.asList(userEntity));

        List<UserDTO> users = searchService.searchUsersByMultipleCriteria(null, email);

        assertEquals(1, users.size());
        assertEquals(email, users.get(0).getEmail());
    }

    @Test
    public void testSearchUsersByMultipleCriteria_NoMatch() {
        String nickname = "john";
        String email = "john@example.com";

        when(userRepository.findAll()).thenReturn(Arrays.asList());

        List<UserDTO> users = searchService.searchUsersByMultipleCriteria(nickname, email);

        assertEquals(0, users.size());
    }
}