package org.example.naumenteststgbot.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.naumenteststgbot.config.BotConfig;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
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
     * Обработчик всех команд
     */
    private final CommandsHandler commandsHandler;

    /**
     * Конфигурация бота
     */
    private final BotConfig config;

    /**
     * Обработчик сообщений
     */
    private final MessageHandler messageHandler;

    public TelegramBot(BotConfig config, CommandsHandler commandsHandler, MessageHandler messageHandler) {
        super(config.getToken());
        this.config = config;
        this.commandsHandler = commandsHandler;
        this.messageHandler = messageHandler;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            if (update.getMessage().getText().startsWith("/")) {
                sendMessage(commandsHandler.handleCommands(update));
            } else {
                sendMessage(messageHandler.handleMessage(update));
            }
        }
    }

    @Override
    public String getBotUsername() {
        return config.getName();
    }

    /**
     * Отправить сообщение
     */
    public void sendMessage(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Не удалось отправить сообщение. {}", e.getMessage());
        }
    }
}
