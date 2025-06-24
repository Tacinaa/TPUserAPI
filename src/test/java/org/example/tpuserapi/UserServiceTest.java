package org.example.tpuserapi;

import org.example.tpuserapi.dto.UserDto;
import org.example.tpuserapi.entity.User;
import org.example.tpuserapi.exception.EmailAlreadyExistsException;
import org.example.tpuserapi.exception.ObjectNotFoundException;
import org.example.tpuserapi.repository.UserRepository;
import org.example.tpuserapi.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void shouldReturnAllUsers() {
        User user = new User();
        user.setId(1L); user.setName("John"); user.setEmail("john@example.com"); user.setPassword("1234");
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDto> users = userService.getAllUsers();

        assertEquals(1, users.size());
        assertEquals("John", users.get(0).getName());
    }

    @Test
    void shouldReturnUserById() {
        User user = new User();
        user.setId(1L); user.setName("John"); user.setEmail("john@example.com"); user.setPassword("1234");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto dto = userService.getUserById(1L);

        assertEquals("John", dto.getName());
        assertEquals("john@example.com", dto.getEmail());
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void shouldCreateUser() {
        User user = new User();
        user.setName("Alice"); user.setEmail("alice@example.com"); user.setPassword("1234");

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.save(any())).thenAnswer(i -> {
            User u = i.getArgument(0);
            u.setId(1L);
            return u;
        });

        UserDto dto = userService.createUser(user);

        assertEquals("Alice", dto.getName());
        assertEquals("alice@example.com", dto.getEmail());
    }

    @Test
    void shouldThrowWhenEmailAlreadyExistsOnCreate() {
        User user = new User();
        user.setName("Alice"); user.setEmail("alice@example.com"); user.setPassword("1234");

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> userService.createUser(user));
    }

    @Test
    void shouldUpdateUser() {
        User existing = new User();
        existing.setId(1L); existing.setName("Old"); existing.setEmail("old@example.com"); existing.setPassword("xxx");

        User updates = new User();
        updates.setName("New"); updates.setEmail("new@example.com"); updates.setPassword("1234");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        UserDto updated = userService.updateUser(1L, updates);

        assertEquals("New", updated.getName());
        assertEquals("new@example.com", updated.getEmail());
    }

    @Test
    void shouldThrowWhenUserNotFoundOnUpdate() {
        User updates = new User();
        updates.setName("X"); updates.setEmail("x@example.com"); updates.setPassword("xxx");

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> userService.updateUser(1L, updates));
    }

    @Test
    void shouldThrowWhenEmailAlreadyUsedOnUpdate() {
        User existing = new User();
        existing.setId(1L); existing.setName("User"); existing.setEmail("user@example.com"); existing.setPassword("xxx");

        User updates = new User();
        updates.setName("User"); updates.setEmail("existing@example.com"); updates.setPassword("xxx");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> userService.updateUser(1L, updates));
    }

    @Test
    void shouldDeleteUser() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void shouldThrowWhenDeletingNonExistentUser() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(ObjectNotFoundException.class, () -> userService.deleteUser(1L));
    }
}