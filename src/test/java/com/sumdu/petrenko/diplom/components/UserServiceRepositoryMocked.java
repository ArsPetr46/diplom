package com.sumdu.petrenko.diplom.components;

import com.sumdu.petrenko.diplom.models.User;
import com.sumdu.petrenko.diplom.repositories.UserRepository;
import com.sumdu.petrenko.diplom.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
public class UserServiceRepositoryMocked {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRetrieveAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(
                new User(1L, "mocked1", "test1@example.com", "Password1", null, null, null, null),
                new User(2L, "mocked2", "test2@example.com", "Password2", null, null, null, null)
        ));

        List<User> users = userService.getAllUsers();
        assertThat(users).hasSize(2);

        User retrievedUser1 = users.get(0);
        User retrievedUser2 = users.get(1);

        assertThat(retrievedUser1.getNickname()).isEqualTo("mocked1");
        assertThat(retrievedUser1.getEmail()).isEqualTo("test1@example.com");
        assertThat(retrievedUser1.getPassword()).isEqualTo("Password1");

        assertThat(retrievedUser2.getNickname()).isEqualTo("mocked2");
        assertThat(retrievedUser2.getEmail()).isEqualTo("test2@example.com");
        assertThat(retrievedUser2.getPassword()).isEqualTo("Password2");
    }
}
