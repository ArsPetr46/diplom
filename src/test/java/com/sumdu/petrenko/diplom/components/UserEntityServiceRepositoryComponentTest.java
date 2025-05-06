package com.sumdu.petrenko.diplom.components;

import com.sumdu.petrenko.diplom.microservices.users.models.UserEntity;
import com.sumdu.petrenko.diplom.microservices.users.repositories.UserRepository;
import com.sumdu.petrenko.diplom.microservices.users.services.UserService;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class UserEntityServiceRepositoryComponentTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
    }

    @Test
    public void testRetrieveAllUsers() {
        UserEntity userEntity1 = new UserEntity(
                "testUser1",
                "test1@example.com",
                "Password1"
        );

        UserEntity userEntity2 = new UserEntity(
                "testUser2",
                "test2@example.com",
                "Password2"
        );

        userService.saveUser(userEntity1);
        userService.saveUser(userEntity2);

        List<UserEntity> userEntities = userService.getAllUsers();
        assertThat(userEntities).hasSize(2);

        UserEntity retrievedUserEntity1 = userEntities.get(0);
        UserEntity retrievedUserEntity2 = userEntities.get(1);

        assertThat(retrievedUserEntity1.getNickname()).isEqualTo("testUser1");
        assertThat(retrievedUserEntity1.getEmail()).isEqualTo("test1@example.com");
        assertThat(retrievedUserEntity1.getPassword()).isEqualTo("Password1");

        assertThat(retrievedUserEntity2.getNickname()).isEqualTo("testUser2");
        assertThat(retrievedUserEntity2.getEmail()).isEqualTo("test2@example.com");
        assertThat(retrievedUserEntity2.getPassword()).isEqualTo("Password2");
    }

    @Test
    public void testRetrieveUserByNickname() {
        UserEntity userEntity = new UserEntity(
                "testUser1",
                "test1@example.com",
                "Password1"
        );

        userService.saveUser(userEntity);

        Optional<UserEntity> retrievedUser = userService.getUserByNickname("testUser1");

        assertThat(retrievedUser).isPresent();
        assertThat(retrievedUser.get().getNickname()).isEqualTo("testUser1");
        assertThat(retrievedUser.get().getEmail()).isEqualTo("test@example.com");
        assertThat(retrievedUser.get().getPassword()).isEqualTo("Password1");
    }

    @Test
    public void testRetrieveUserByEmail() {
        UserEntity userEntity = new UserEntity(
                "testUser1",
                "test1@example.com",
                "Password1"
        );

        userService.saveUser(userEntity);

        Optional<UserEntity> retrievedUser = userService.getUserByEmail("test@example.com");

        assertThat(retrievedUser).isPresent();
        assertThat(retrievedUser.get().getNickname()).isEqualTo("testUser1");
        assertThat(retrievedUser.get().getEmail()).isEqualTo("test@example.com");
        assertThat(retrievedUser.get().getPassword()).isEqualTo("Password1");
    }

    @Test
    public void testSaveUserWithMaxLengthFields() {
        UserEntity userEntity = new UserEntity(
                "a".repeat(30),
                "test".repeat(15) + "@example.com",
                "password".repeat(3) + "1".repeat(6)
        );

        userService.saveUser(userEntity);

        Optional<UserEntity> retrievedUser = userService.getUserByNickname("a".repeat(30));

        assertThat(retrievedUser).isPresent();
        assertThat(retrievedUser.get().getNickname()).isEqualTo("a".repeat(30));
        assertThat(retrievedUser.get().getEmail()).isEqualTo("test".repeat(15) + "@example.com");
        assertThat(retrievedUser.get().getPassword()).isEqualTo("password".repeat(3) + "1".repeat(6));
    }

    @Test
    public void testSaveUserWithEmptyFields() {
        UserEntity userEntity = new UserEntity("", "", "");

        assertThrows(ConstraintViolationException.class, () -> {
            userService.saveUser(userEntity);
        });
    }

    @Test
    public void testSaveUserWithNullFields() {
        UserEntity userEntity = new UserEntity(null, null, null);

        assertThrows(DataIntegrityViolationException.class, () -> {
            userService.saveUser(userEntity);
        });
    }

    @Test
    public void testSaveUserWithInvalidNickname() {
        UserEntity userEntity = new UserEntity(
                "testUser***",
                "test1@example.com",
                "Password1"
        );

        assertThrows(ConstraintViolationException.class, () -> {
            userService.saveUser(userEntity);
        });
    }

    @Test
    public void testSaveUserWithInvalidEmailFormat() {
        UserEntity userEntity = new UserEntity(
                "testUser",
                "invalidemail",
                "Password1"
        );

        assertThrows(ConstraintViolationException.class, () -> {
            userService.saveUser(userEntity);
        });
    }

    @Test
    public void testSaveUserWithInvalidPassword() {
        UserEntity userEntity = new UserEntity(
                "testUser",
                "test1@example.com",
                "password&&&&"
        );

        assertThrows(ConstraintViolationException.class, () -> {
            userService.saveUser(userEntity);
        });
    }

    @Test
    public void testDuplicateUser() {
        UserEntity userEntity1 = new UserEntity(
                "testUser",
                "test1@example.com",
                "Password1"
        );

        userService.saveUser(userEntity1);

        UserEntity userEntity2 = new UserEntity(
                "testUser",
                "test1@example.com",
                "Password1"
        );

        assertThrows(DataIntegrityViolationException.class, () -> {
            userService.saveUser(userEntity2);
        });
    }

    @Test
    public void testUpdateUser() {
        UserEntity userEntity = new UserEntity(
                "testUser1",
                "test1@example.com",
                "Password1"
        );

        userService.saveUser(userEntity);

        Optional<UserEntity> savedUser = userService.getUserByNickname("testUser1");
        assertThat(savedUser).isPresent();

        UserEntity updatedUserEntity = new UserEntity(
                savedUser.get().getId(),
                "testUser1",
                "updated@example.com",
                "Password1"
        );

        userService.saveUser(userEntity);

        Optional<UserEntity> retrievedUser = userService.getUserByNickname("testUser1");

        assertThat(retrievedUser).isPresent();
        assertThat(retrievedUser.get().getNickname()).isEqualTo("testUser1");
        assertThat(retrievedUser.get().getEmail()).isEqualTo("updated@example.com");
        assertThat(retrievedUser.get().getPassword()).isEqualTo("Password1");
    }

    @Test
    public void testDeleteUser() {
        UserEntity userEntity = new UserEntity(
                "testUser",
                "test1@example.com",
                "Password1"
        );

        Optional<UserEntity> savedUser = userService.getUserByNickname("testUser");
        assertThat(savedUser).isPresent();

        userService.deleteUser(savedUser.get().getId());

        Optional<UserEntity> retrievedUser = userService.getUserById(userEntity.getId());
        assertThat(retrievedUser).isNotPresent();
    }
}