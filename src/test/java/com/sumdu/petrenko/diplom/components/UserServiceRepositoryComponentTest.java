package com.sumdu.petrenko.diplom.components;

import com.sumdu.petrenko.diplom.models.User;
import com.sumdu.petrenko.diplom.repositories.UserRepository;
import com.sumdu.petrenko.diplom.services.UserService;
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
public class UserServiceRepositoryComponentTest {

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
        User user1 = new User(
                "testUser1",
                "test1@example.com",
                "Password1"
        );

        User user2 = new User(
                "testUser2",
                "test2@example.com",
                "Password2"
        );

        userService.saveUser(user1);
        userService.saveUser(user2);

        List<User> users = userService.getAllUsers();
        assertThat(users).hasSize(2);

        User retrievedUser1 = users.get(0);
        User retrievedUser2 = users.get(1);

        assertThat(retrievedUser1.getNickname()).isEqualTo("testUser1");
        assertThat(retrievedUser1.getEmail()).isEqualTo("test1@example.com");
        assertThat(retrievedUser1.getPassword()).isEqualTo("Password1");

        assertThat(retrievedUser2.getNickname()).isEqualTo("testUser2");
        assertThat(retrievedUser2.getEmail()).isEqualTo("test2@example.com");
        assertThat(retrievedUser2.getPassword()).isEqualTo("Password2");
    }

    @Test
    public void testRetrieveUserByNickname() {
        User user = new User(
                "testUser1",
                "test1@example.com",
                "Password1"
        );

        userService.saveUser(user);

        Optional<User> retrievedUser = userService.getUserByNickname("testUser1");

        assertThat(retrievedUser).isPresent();
        assertThat(retrievedUser.get().getNickname()).isEqualTo("testUser1");
        assertThat(retrievedUser.get().getEmail()).isEqualTo("test@example.com");
        assertThat(retrievedUser.get().getPassword()).isEqualTo("Password1");
    }

    @Test
    public void testRetrieveUserByEmail() {
        User user = new User(
                "testUser1",
                "test1@example.com",
                "Password1"
        );

        userService.saveUser(user);

        Optional<User> retrievedUser = userService.getUserByEmail("test@example.com");

        assertThat(retrievedUser).isPresent();
        assertThat(retrievedUser.get().getNickname()).isEqualTo("testUser1");
        assertThat(retrievedUser.get().getEmail()).isEqualTo("test@example.com");
        assertThat(retrievedUser.get().getPassword()).isEqualTo("Password1");
    }

    @Test
    public void testSaveUserWithMaxLengthFields() {
        User user = new User(
                "a".repeat(30),
                "test".repeat(15) + "@example.com",
                "password".repeat(3) + "1".repeat(6)
        );

        userService.saveUser(user);

        Optional<User> retrievedUser = userService.getUserByNickname("a".repeat(30));

        assertThat(retrievedUser).isPresent();
        assertThat(retrievedUser.get().getNickname()).isEqualTo("a".repeat(30));
        assertThat(retrievedUser.get().getEmail()).isEqualTo("test".repeat(15) + "@example.com");
        assertThat(retrievedUser.get().getPassword()).isEqualTo("password".repeat(3) + "1".repeat(6));
    }

    @Test
    public void testSaveUserWithEmptyFields() {
        User user = new User("", "", "");

        assertThrows(ConstraintViolationException.class, () -> {
            userService.saveUser(user);
        });
    }

    @Test
    public void testSaveUserWithNullFields() {
        User user = new User(null, null, null);

        assertThrows(DataIntegrityViolationException.class, () -> {
            userService.saveUser(user);
        });
    }

    @Test
    public void testSaveUserWithInvalidNickname() {
        User user = new User(
                "testUser***",
                "test1@example.com",
                "Password1"
        );

        assertThrows(ConstraintViolationException.class, () -> {
            userService.saveUser(user);
        });
    }

    @Test
    public void testSaveUserWithInvalidEmailFormat() {
        User user = new User(
                "testUser",
                "invalidemail",
                "Password1"
        );

        assertThrows(ConstraintViolationException.class, () -> {
            userService.saveUser(user);
        });
    }

    @Test
    public void testSaveUserWithInvalidPassword() {
        User user = new User(
                "testUser",
                "test1@example.com",
                "password&&&&"
        );

        assertThrows(ConstraintViolationException.class, () -> {
            userService.saveUser(user);
        });
    }

    @Test
    public void testDuplicateUser() {
        User user1 = new User(
                "testUser",
                "test1@example.com",
                "Password1"
        );

        userService.saveUser(user1);

        User user2 = new User(
                "testUser",
                "test1@example.com",
                "Password1"
        );

        assertThrows(DataIntegrityViolationException.class, () -> {
            userService.saveUser(user2);
        });
    }

    @Test
    public void testUpdateUser() {
        User user = new User(
                "testUser1",
                "test1@example.com",
                "Password1"
        );

        userService.saveUser(user);

        Optional<User> savedUser = userService.getUserByNickname("testUser1");
        assertThat(savedUser).isPresent();

        User updatedUser = new User(
                savedUser.get().getId(),
                "testUser1",
                "updated@example.com",
                "Password1"
        );

        userService.saveUser(user);

        Optional<User> retrievedUser = userService.getUserByNickname("testUser1");

        assertThat(retrievedUser).isPresent();
        assertThat(retrievedUser.get().getNickname()).isEqualTo("testUser1");
        assertThat(retrievedUser.get().getEmail()).isEqualTo("updated@example.com");
        assertThat(retrievedUser.get().getPassword()).isEqualTo("Password1");
    }

    @Test
    public void testDeleteUser() {
        User user = new User(
                "testUser",
                "test1@example.com",
                "Password1"
        );

        Optional<User> savedUser = userService.getUserByNickname("testUser");
        assertThat(savedUser).isPresent();

        userService.deleteUser(savedUser.get().getId());

        Optional<User> retrievedUser = userService.getUserById(user.getId());
        assertThat(retrievedUser).isNotPresent();
    }
}