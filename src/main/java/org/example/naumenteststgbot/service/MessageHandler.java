package org.example.naumenteststgbot.service;

import org.example.naumenteststgbot.entity.UserSession;
import org.example.naumenteststgbot.entity.UserState;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Обработчик сообщений
 */
@Service
public class MessageHandler {
    private final UserService userService;
    private final TestService testService;

    public MessageHandler(UserService userService, TestService testService) {
        this.userService = userService;
        this.testService = testService;
    }

    /**
     * Обработать сообщение
     */
    public SendMessage handleMessage(Update update) {
        UserSession userSession = userService.getSession(update.getMessage().getFrom().getId());
        UserState userState = userSession.getState();
        String text = update.getMessage().getText();
        String responseMessage = "Я вас не понимаю, для справки используйте /help";
        if(userState != UserState.DEFAULT) {
            responseMessage = testService.getResponseMessage(userSession, text);
        }

        return new SendMessage(update.getMessage().getChatId().toString(), responseMessage);
    }
}
