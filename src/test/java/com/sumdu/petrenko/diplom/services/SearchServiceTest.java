package com.sumdu.petrenko.diplom.services;

import com.sumdu.petrenko.diplom.dto.UserDTO;
import com.sumdu.petrenko.diplom.models.User;
import com.sumdu.petrenko.diplom.repositories.UserRepository;
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
        User user = new User();
        user.setNickname(nickname);
        user.setEmail(email);

        when(userRepository.findAll()).thenReturn(Arrays.asList(user));

        List<UserDTO> users = searchService.searchUsersByMultipleCriteria(nickname, email);

        assertEquals(1, users.size());
        assertEquals(nickname, users.get(0).getNickname());
        assertEquals(email, users.get(0).getEmail());
    }

    @Test
    public void testSearchUsersByMultipleCriteria_NicknameOnly() {
        String nickname = "john";
        User user = new User();
        user.setNickname(nickname);
        user.setEmail("john@example.com");

        when(userRepository.findAll()).thenReturn(Arrays.asList(user));

        List<UserDTO> users = searchService.searchUsersByMultipleCriteria(nickname, null);

        assertEquals(1, users.size());
        assertEquals(nickname, users.get(0).getNickname());
    }

    @Test
    public void testSearchUsersByMultipleCriteria_EmailOnly() {
        String email = "john@example.com";
        User user = new User();
        user.setNickname("john");
        user.setEmail(email);

        when(userRepository.findAll()).thenReturn(Arrays.asList(user));

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