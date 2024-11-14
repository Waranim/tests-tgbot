package org.example.naumenteststgbot.service;

import org.example.naumenteststgbot.config.BotConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Тесты для телеграм бота
 */
@ExtendWith(MockitoExtension.class)
class TelegramBotTest {

    /**
     * Конфигурация телеграм бота
     */
    @Mock
    private BotConfig config;

    /**
     * Телеграм бот
     */
    @InjectMocks
    private TelegramBot telegramBot;

    /**
     * Тест на получение имени телеграм бота
     */
    @Test
    public void testGetBotUsername() {
        when(config.getName()).thenReturn("testBot");

        String username = telegramBot.getBotUsername();

        assertEquals("testBot", username);
    }
}