package org.example.naumenteststgbot.service;

import org.example.naumenteststgbot.config.BotConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

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
     * Обработчик всех команд
     */
    @Mock
    private CommandsHandler commandsHandler;

    /**
     * Отправка сообщений
     */
    @Mock
    private MessageSender messageSender;

    /**
     * Обработка сообщений
     */
    @Mock
    private MessageHandler messageHandler;

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

    /**
     * Тест обработки команды
     */
    @Test
    public void testOnUpdateReceivedWithCommand() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);

        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.hasText()).thenReturn(true);
        when(message.getText()).thenReturn("/start");
        doNothing().when(messageSender).sendMessage(any(SendMessage.class));

        String text = """
                Здравствуйте. Я бот специализирующийся на создании и прохождении тестов. Доступны следующие команды:
                /add – Добавить тест
                /add_question [testID] - Добавить вопрос к тесту
                /view – Посмотреть список тестов
                /view [testID] - Посмотреть тест
                /view_question [testID] - Посмотреть список вопросов к тесту
                /edit [testID] - Изменить тест с номером testID
                /edit_question [questionID] - Изменить вопрос с номером questionID
                /del [testID] – Удалить тест с номером testID
                /del_question [questionID] - Удалить вопрос с номером questionID
                /stop - Закончить ввод вариантов ответа, если добавлено минимум 2 варианта \
                при выполнении команды /add_question [testID]
                /help - Справка""";
        SendMessage expectedMessage = new SendMessage("12345", text);
        when(commandsHandler.handleCommands(update)).thenReturn(expectedMessage);

        telegramBot.onUpdateReceived(update);

        verify(messageSender).sendMessage(expectedMessage);
    }

    /**
     * Тест обработки некорректного текста
     */
    @Test
    public void testOnUpdateReceivedWithNonCommandText() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);

        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.hasText()).thenReturn(true);
        when(message.getText()).thenReturn("Hello");

        SendMessage expectedMessage = new SendMessage("12345", "Я вас не понимаю, для справки используйте /help");
        when(messageHandler.handleMessage(update)).thenReturn(expectedMessage);

        telegramBot.onUpdateReceived(update);

        verify(messageSender).sendMessage(expectedMessage);
    }

    /**
     * Тест, когда сообщение не содержит текст
     */
    @Test
    public void testOnUpdateReceivedNoTextMessage() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);

        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.hasText()).thenReturn(false);

        telegramBot.onUpdateReceived(update);

        verify(messageSender, never()).sendMessage(any(SendMessage.class));
    }
}