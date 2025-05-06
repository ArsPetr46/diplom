package com.sumdu.petrenko.diplom.components;

import com.sumdu.petrenko.diplom.microservices.users.models.UserEntity;
import com.sumdu.petrenko.diplom.microservices.users.repositories.UserRepository;
import com.sumdu.petrenko.diplom.microservices.users.services.UserService;
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
public class UserEntityServiceRepositoryMocked {

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
                new UserEntity(1L, "mocked1", "test1@example.com", "Password1", null, null, null, null),
                new UserEntity(2L, "mocked2", "test2@example.com", "Password2", null, null, null, null)
        ));

        List<UserEntity> userEntities = userService.getAllUsers();
        assertThat(userEntities).hasSize(2);

        UserEntity retrievedUserEntity1 = userEntities.get(0);
        UserEntity retrievedUserEntity2 = userEntities.get(1);

        assertThat(retrievedUserEntity1.getNickname()).isEqualTo("mocked1");
        assertThat(retrievedUserEntity1.getEmail()).isEqualTo("test1@example.com");
        assertThat(retrievedUserEntity1.getPassword()).isEqualTo("Password1");

        assertThat(retrievedUserEntity2.getNickname()).isEqualTo("mocked2");
        assertThat(retrievedUserEntity2.getEmail()).isEqualTo("test2@example.com");
        assertThat(retrievedUserEntity2.getPassword()).isEqualTo("Password2");
    }
}
