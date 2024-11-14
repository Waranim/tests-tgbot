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
    private final QuestionService questionService;

    public MessageHandler(UserService userService, TestService testService, QuestionService questionService) {
        this.userService = userService;
        this.testService = testService;
        this.questionService = questionService;
    }

    /**
     * Обработать сообщение
     */
    public SendMessage handleMessage(Update update) {
        UserSession userSession = userService.getSession(update.getMessage().getFrom().getId());
        UserState userState = userSession.getState();
        String text = update.getMessage().getText();
        String responseMessage = "Я вас не понимаю, для справки используйте /help";

        switch (userState) {
            case ADD_TEST_TITLE:
            case ADD_TEST_DESCRIPTION:
            case EDIT_TEST:
            case EDIT_TEST_TITLE:
            case EDIT_TEST_DESCRIPTION:
            case DELETE_TEST:
            case CONFIRM_DELETE_TEST:
                responseMessage = testService.handleMessage(userSession, text);
                break;

            case ADD_QUESTION_TEXT:
            case ADD_QUESTION:
            case ADD_ANSWER:
            case SET_CORRECT_ANSWER:
            case EDIT_QUESTION:
            case EDIT_QUESTION_TEXT:
            case EDIT_ANSWER_OPTION:
            case EDIT_ANSWER_OPTION_CHOICE:
            case EDIT_ANSWER_TEXT_CHOICE:
            case EDIT_ANSWER_TEXT:
            case DELETE_QUESTION:
            case CONFIRM_DELETE_QUESTION:
                responseMessage = questionService.getResponseMessage(userSession, text);
                break;

            default:
                break;
        }

        return new SendMessage(update.getMessage().getChatId().toString(), responseMessage);
    }
}
