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

    private final TestService testService;

    private final QuestionService questionService;

    public CommandsHandler(HelpHandler helpHandler, UserService userService, TestService testService, QuestionService questionService) {
        this.helpHandler = helpHandler;
        this.userService = userService;
        this.testService = testService;
        this.questionService = questionService;
    }

    /**
     * Обрабатывает команду
     *
     * @return сообщение для отправки пользователю
     */
    public SendMessage handleCommands(Update update) {
        User user = update.getMessage().getFrom();
        String messageText = update.getMessage().getText();
        String command = messageText.split(" ")[0];
        String replyText;

        switch (command) {
            case "/start":
                userService.create(user.getId(), user.getUserName());
                replyText = helpHandler.handle();
                break;
            case "/help":
                replyText = helpHandler.handle();
                break;
            case "/add":
                replyText = testService.handleAdd(user.getId());
                break;
            case "/view":
                replyText = testService.handleView(user.getId(), messageText);
                break;
            case "/edit":
                replyText = testService.handleEdit(user.getId(), messageText);
                break;
            case "/del":
                replyText = testService.handleDel(user.getId());
                break;
            case "/add_question":
                replyText =questionService.handleAddQuestion(user.getId(),messageText);
                break;
            case "/view_question":
                replyText = questionService.handleViewQuestions(user.getId(), messageText);
                break;
            case "/edit_question":
                replyText = questionService.handleEditQuestion(user.getId(), messageText);
                break;
            case "/del_question":
                replyText = questionService.handleDeleteQuestion(user.getId(),messageText);
                break;
            case "/stop":
                replyText = questionService.handleStop(user.getId());
                break;
            default:
                replyText = "Неверная команда, для справки используйте /help";
                break;
        }

        return new SendMessage(update.getMessage().getChatId().toString(), replyText);
    }
}
