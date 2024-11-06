package org.example.naumenteststgbot.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Тесты для обработчика команд
 */
@ExtendWith(MockitoExtension.class)
class CommandsHandlerTest {

    /**
     * Обработчик команд
     */
    @InjectMocks
    private CommandsHandler commandHandler;

    /**
     * Сервис для взаимодействия с сущностью пользователя
     */
    @Mock
    private UserService userService;

    /**
     * Обработчик команд /start и /help
     */
    @Mock
    private HelpHandler helpHandler;

    /**
     * Тест на первый запуск бота пользователем
     */
    @Test
    public void testHandleStartCommand() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        User user = new User();
        user.setId(1L);
        user.setUserName("testUser");

        when(update.getMessage()).thenReturn(message);
        when(message.getText()).thenReturn("/start");
        when(message.getFrom()).thenReturn(user);
        when(message.getChatId()).thenReturn(12345L);
        when(helpHandler.handle()).thenReturn("Help");

        SendMessage response = commandHandler.handleCommands(update);

        verify(userService).create(1L, "testUser");
        assertEquals("12345", response.getChatId());
        assertEquals("Help", response.getText());
    }

    /**
     * Тест на выполнение команды /help пользователем
     */
    @Test
    public void testHandleHelpCommand() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);

        when(update.getMessage()).thenReturn(message);
        when(message.getText()).thenReturn("/help");
        when(message.getChatId()).thenReturn(12345L);
        when(helpHandler.handle()).thenReturn("Help");

        SendMessage response = commandHandler.handleCommands(update);

        assertEquals("12345", response.getChatId());
        assertEquals("Help", response.getText());
    }

    /**
     * Тест на использование неизвестной команды пользователем
     */
    @Test
    public void testHandleUnknownCommand() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);

        when(update.getMessage()).thenReturn(message);
        when(message.getText()).thenReturn("/about");
        when(message.getChatId()).thenReturn(12345L);

        SendMessage response = commandHandler.handleCommands(update);

        assertEquals("12345", response.getChatId());
        assertEquals("Неверная команда, для справки используйте /help", response.getText());
    }
}