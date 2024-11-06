package org.example.naumenteststgbot.service;

import org.example.naumenteststgbot.DTO.UserDTO;
import org.example.naumenteststgbot.entity.UserEntity;
import org.example.naumenteststgbot.repository.UserRepository;
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
                user.getId().equals(id) && user.getUsername().equals(username)));
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
        existingUser.setId(id);
        existingUser.setUsername(username);

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));

        userService.create(id, username);

        verify(userRepository, never()).save(any(UserEntity.class));
    }


    @Test
    void testGetUserWhenUserExists() {
        Long id = 12345L;
        String username = "testuser";
        UserEntity existingUser = new UserEntity();
        existingUser.setId(id);
        existingUser.setUsername(username);

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));

        UserDTO userDTO = userService.get(id);
        assertNotNull(userDTO);
        assertEquals(id, userDTO.id());
        assertEquals(username, userDTO.username());
    }

    @Test
    void testGetUserWhenUserDoesNotExist() {
        Long id = 12345L;

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        UserDTO userDTO = userService.get(id);
        assertNull(userDTO);
    }
}