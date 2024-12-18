package org.example.bot.service;

import org.example.bot.entity.UserEntity;
import org.example.bot.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Тесты сервиса для взаимодействия с сущностью пользователя
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    /**
     * Репозиторий для работы с пользователем в базе данных
     */
    @Mock
    private UserRepository userRepository;

    /**
     * Сервис для взаимодействия с сущностью пользователя
     */
    @InjectMocks
    private UserService userService;

    /**
     * Тест на создание и сохранение пользователя в базе данных
     */
    @Test
    void testCreateUserWhenSaveUser() {
        Long id = 12345L;
        String username = "testuser";

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        userService.create(id, username);

        verify(userRepository, times(1)).save(argThat(user ->
                user.getUserId().equals(id) && user.getUsername().equals(username)));
    }

    /**
     * Тест на отсутствие сохранения новой записи пользователя в базе данных,
     * если пользователь с конкретным идентификатором был найден
     */
    @Test
    void testCreateUserWhenDoNotSaveUser() {
        Long id = 12345L;
        String username = "testuser";
        UserEntity existingUser = new UserEntity();
        existingUser.setUserId(id);
        existingUser.setUsername(username);

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));

        userService.create(id, username);

        verify(userRepository, never()).save(any(UserEntity.class));
    }

    /**
     * Тест на получение пользователя, когда пользователь существует в базе данных
     */
    @Test
    void testGetUserWhenUserExists() {
        Long id = 12345L;
        String username = "testuser";
        UserEntity existingUser = new UserEntity();
        existingUser.setUserId(id);
        existingUser.setUsername(username);

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));

        Optional<UserEntity> optionalUser = userService.getUserById(id);
        assertTrue(optionalUser.isPresent());
        UserEntity user = optionalUser.get();
        assertEquals(id, user.getUserId());
        assertEquals(username, user.getUsername());
    }

    /**
     * Тест на получение пользователя, когда пользователь не существует в базе данных
     */
    @Test
    void testGetUserWhenUserDoesNotExist() {
        Long id = 12345L;

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        Optional<UserEntity> user = userService.getUserById(id);
        assertTrue(user.isEmpty());
    }
}