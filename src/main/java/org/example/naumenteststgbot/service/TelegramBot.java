package org.example.naumenteststgbot.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.naumenteststgbot.config.BotConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Телеграм бот
 */
@Service
public class TelegramBot extends TelegramLongPollingBot {

    /**
     * Логер
     */
    private final Logger log = LogManager.getLogger(TelegramBot.class);

    /**
     * Конфигурация бота
     */
    private final BotConfig config;

    /**
     * Обработчик сообщений
     */
    private final MessageHandler messageHandler;

    /**
     * Отправка сообщений
     */
    private final MessageSender messageSender;

    /**
     * Конструктор для инициализации бота
     * @param config конфигурация бота
     * @param messageHandler обработчик сообщений
     */
    public TelegramBot(BotConfig config, MessageHandler messageHandler) {
        super(config.getToken());
        this.config = config;
        this.messageHandler = messageHandler;

        this.messageSender = message -> {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error("Не удалось отправить сообщение: {}", e.getMessage());
            }
        };
    }

    /**
     * Обработка входящих обновлений от Telegram API
     * @param update объект, содержащий информацию о взаимодействии пользователя с ботом (сообщения, callback и др.)
     */
    @Transactional
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            User user = update.getMessage().getFrom();
            Long userId = user.getId();
            String username = user.getUserName();
            String chatId = update.getMessage().getChatId().toString();
            if (message.equals("/start"))
                message += " " + username;
            messageSender.sendMessage(new SendMessage(chatId, messageHandler.handle(message, userId)));
        }
    }

    /**
     * Получение имени бота
     * @return имя бота из конфигурации
     */
    @Override
    public String getBotUsername() {
        return config.getName();
    }
}
