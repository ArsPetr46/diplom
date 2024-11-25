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
        User user1 = new User();
        user1.setNickname("testUser1");
        user1.setEmail("test1@example.com");
        user1.setPassword("Password1");

        User user2 = new User();
        user2.setNickname("testUser2");
        user2.setEmail("test2@example.com");
        user2.setPassword("Password2");

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
        User user = new User();
        user.setNickname("testUser1");
        user.setEmail("test@example.com");
        user.setPassword("Password1");

        userService.saveUser(user);

        Optional<User> retrievedUser = userService.getUserByNickname("testUser1");
        assertThat(retrievedUser).isPresent();
        assertThat(retrievedUser.get().getNickname()).isEqualTo("testUser1");
        assertThat(retrievedUser.get().getEmail()).isEqualTo("test@example.com");
        assertThat(retrievedUser.get().getPassword()).isEqualTo("Password1");
    }

    @Test
    public void testRetrieveUserByEmail() {
        User user = new User();
        user.setNickname("testUser1");
        user.setEmail("test@example.com");
        user.setPassword("Password1");

        userService.saveUser(user);

        Optional<User> retrievedUser = userService.getUserByEmail("test@example.com");
        assertThat(retrievedUser).isPresent();
        assertThat(retrievedUser.get().getNickname()).isEqualTo("testUser1");
        assertThat(retrievedUser.get().getEmail()).isEqualTo("test@example.com");
        assertThat(retrievedUser.get().getPassword()).isEqualTo("Password1");
    }

    @Test
    public void testSaveAndRetrieveUser() {
        User user = new User();
        user.setNickname("testUser1");
        user.setEmail("test@example.com");
        user.setPassword("Password1");

        userService.saveUser(user);

        Optional<User> retrievedUser = userService.getUserByNickname("testUser1");
        assertThat(retrievedUser).isPresent();
        assertThat(retrievedUser.get().getNickname()).isEqualTo("testUser1");
        assertThat(retrievedUser.get().getEmail()).isEqualTo("test@example.com");
        assertThat(retrievedUser.get().getPassword()).isEqualTo("Password1");
    }

    @Test
    public void testSaveUserWithMaxLengthFields() {
        User user = new User();
        user.setNickname("a".repeat(30));
        user.setEmail("test".repeat(15) + "@example.com");
        user.setPassword("password".repeat(3) + "1".repeat(6));

        userService.saveUser(user);

        Optional<User> retrievedUser = userService.getUserByNickname("a".repeat(30));
        assertThat(retrievedUser).isPresent();
        assertThat(retrievedUser.get().getNickname()).isEqualTo("a".repeat(30));
        assertThat(retrievedUser.get().getEmail()).isEqualTo("test".repeat(15) + "@example.com");
        assertThat(retrievedUser.get().getPassword()).isEqualTo("password".repeat(3) + "1".repeat(6));
    }

    @Test
    public void testSaveUserWithEmptyFields() {
        User user = new User();
        user.setNickname("");
        user.setEmail("");
        user.setPassword("");

        assertThrows(ConstraintViolationException.class, () -> {
            userService.saveUser(user);
        });
    }

    @Test
    public void testSaveUserWithNullFields() {
        User user = new User();
        user.setNickname(null);
        user.setEmail(null);
        user.setPassword(null);

        assertThrows(DataIntegrityViolationException.class, () -> {
            userService.saveUser(user);
        });
    }

    @Test
    public void testSaveUserWithInvalidNickname() {
        User user = new User();
        user.setNickname("testUser***");
        user.setEmail("test@example.com");
        user.setPassword("Password1");

        assertThrows(ConstraintViolationException.class, () -> {
            userService.saveUser(user);
        });
    }

    @Test
    public void testSaveUserWithInvalidEmailFormat() {
        User user = new User();
        user.setNickname("testUser");
        user.setEmail("invalidEmail");
        user.setPassword("Password1");

        assertThrows(ConstraintViolationException.class, () -> {
            userService.saveUser(user);
        });
    }

    @Test
    public void testSaveUserWithInvalidPassword() {
        User user = new User();
        user.setNickname("testUser");
        user.setEmail("test@example.com");
        user.setPassword("password&&");

        assertThrows(ConstraintViolationException.class, () -> {
            userService.saveUser(user);
        });
    }

    @Test
    public void testDuplicateUser() {
        User user1 = new User();
        user1.setNickname("testUser");
        user1.setEmail("test@example.com");
        user1.setPassword("Password1");

        userService.saveUser(user1);

        User user2 = new User();
        user2.setNickname("testUser");
        user2.setEmail("test@example.com");
        user2.setPassword("Password2");

        assertThrows(DataIntegrityViolationException.class, () -> {
            userService.saveUser(user2);
        });
    }

    @Test
    public void testUpdateUser() {
        User user = new User();
        user.setNickname("testUser1");
        user.setEmail("test@example.com");
        user.setPassword("Password1");

        userService.saveUser(user);

        user.setEmail("updated@example.com");
        userService.saveUser(user);

        Optional<User> retrievedUser = userService.getUserByNickname("testUser1");
        assertThat(retrievedUser).isPresent();
        assertThat(retrievedUser.get().getNickname()).isEqualTo("testUser1");
        assertThat(retrievedUser.get().getEmail()).isEqualTo("updated@example.com");
        assertThat(retrievedUser.get().getPassword()).isEqualTo("Password1");
    }

    @Test
    public void testDeleteUser() {
        User user = new User();
        user.setNickname("testUser1");
        user.setEmail("test@example.com");
        user.setPassword("Password1");

        userService.saveUser(user);
        userService.deleteUser(user.getId());

        Optional<User> retrievedUser = userService.getUserById(user.getId());
        assertThat(retrievedUser).isNotPresent();
    }
}