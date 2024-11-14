package org.example.naumenteststgbot.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.naumenteststgbot.config.BotConfig;
import org.springframework.beans.factory.annotation.Autowired;
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
     * Отправка сообщений
     */
    private final MessageSender messageSender;

    public TelegramBot(BotConfig config, CommandsHandler commandsHandler, MessageSender messageSender) {
        super(config.getToken());
        this.config = config;
        this.commandsHandler = commandsHandler;
        this.messageSender = messageSender;
    }

    @Autowired
    public TelegramBot(BotConfig config, CommandsHandler commandsHandler) {
        super(config.getToken());
        this.config = config;
        this.commandsHandler = commandsHandler;

        this.messageSender = message -> {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error("Не удалось отправить сообщение: {}", e.getMessage());
            }
        };
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            if (update.getMessage().getText().startsWith("/")) {
                messageSender.sendMessage(commandsHandler.handleCommands(update));
            } else {
                messageSender.sendMessage(new SendMessage(chatId, "Я вас не понимаю, для справки используйте /help"));
            }
        }
    }

    @Override
    public String getBotUsername() {
        return config.getName();
    }
}
