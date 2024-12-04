package org.example.naumenteststgbot.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.naumenteststgbot.config.BotConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
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

    /**
     * Обработчик Callback query
     */
    private final CallbackQueryHandler callbackQueryHandler;

    /**
     * Отправка сообщений
     */
    private final MessageSender messageSender;

    /**
     * Редактирование сообщений
     */
    private final MessageEditor messageEditor;

    public TelegramBot(BotConfig config, CommandsHandler commandsHandler, MessageSender messageSender, MessageHandler messageHandler, CallbackQueryHandler callbackQueryHandler, UserService userService, MessageEditor messageEditor) {
        super(config.getToken());
        this.config = config;
        this.commandsHandler = commandsHandler;
        this.messageSender = messageSender;
        this.messageHandler = messageHandler;
        this.callbackQueryHandler = callbackQueryHandler;
        this.messageEditor = messageEditor;
    }

    @Autowired
    public TelegramBot(BotConfig config, CommandsHandler commandsHandler, MessageHandler messageHandler, CallbackQueryHandler callbackQueryHandler, UserService userService) {
        super(config.getToken());
        this.config = config;
        this.commandsHandler = commandsHandler;
        this.messageHandler = messageHandler;
        this.callbackQueryHandler = callbackQueryHandler;

        this.messageEditor = message -> {
            try {
                if(message != null)
                    execute(message);
            } catch (TelegramApiException e) {
                log.error("Не удалось отредактировать сообщение: {}", e.getMessage());
            }
        };

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
            if (update.getMessage().getText().startsWith("/")) {
                messageSender.sendMessage(commandsHandler.handleCommands(update));
            } else {
                messageSender.sendMessage(messageHandler.handleMessage(update));
            }
        }
        else if(update.hasCallbackQuery()){
            if(update.getCallbackQuery().getData().startsWith("EDIT"))
                messageEditor.editMessage(callbackQueryHandler.handleEdit(update));
            else
                messageSender.sendMessage(callbackQueryHandler.handle(update));
        }
    }

    @Override
    public String getBotUsername() {
        return config.getName();
    }
}
