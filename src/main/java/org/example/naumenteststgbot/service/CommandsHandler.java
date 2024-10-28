package org.example.naumenteststgbot.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

/**
 * Обработчик всех команд
 */
@Service
public class CommandsHandler {

    /**
     * Обработчик команд /start и /help
     */
    private final HelpHandler helpHandler;
    /**
     * Сервис для взаимодействия с пользователем
     */
    private final UserService userService;

    public CommandsHandler(HelpHandler helpHandler, UserService userService) {
        this.helpHandler = helpHandler;
        this.userService = userService;
    }

    /**
     * Обрабатывает команду
     *
     * @return сообщение для отправки пользователю
     */
    public SendMessage handleCommands(Update update) {
        String messageText = update.getMessage().getText();
        String command = messageText.split(" ")[0];
        String replyText;
        switch (command) {
            case "/start":
                User user = update.getMessage().getFrom();
                userService.create(user.getId(), user.getUserName());
                replyText = helpHandler.handle();
                break;
            case "/help":
                replyText = helpHandler.handle();
                break;
            default:
                replyText = "Неверная команда, для справки используйте /help";
                break;
        }

        return new SendMessage(update.getMessage().getChatId().toString(), replyText);
    }
}
