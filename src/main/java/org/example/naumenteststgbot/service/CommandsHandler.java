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

    /**
     * Сервис для взаимодействия с тестами
     */
    private final TestService testService;

    /**
     * Сервис для взаимодействия с вопросами
     */
    private final QuestionService questionService;

    /**
     * Конструктор класса CommandsHandler
     */
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
        String chatId = update.getMessage().getChatId().toString();
        String replyText = "Ошибка";
        SendMessage replyMessage = null;

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
                replyMessage = testService.handleView(chatId,user.getId(), messageText);
                break;
            case "/edit":
                replyMessage = testService.handleEdit(chatId,user.getId(), messageText);
                break;
            case "/del":
                replyMessage = testService.handleDel(chatId, user.getId());
                break;
            case "/add_question":
                replyText = questionService.handleAddQuestion(user.getId(), messageText);
                break;
            case "/view_question":
                replyText = questionService.handleViewQuestions(user.getId(), messageText);
                break;
            case "/edit_question":
                replyMessage = questionService.handleEditQuestion(chatId, user.getId(), messageText);
                break;
            case "/del_question":
                replyMessage = questionService.handleDeleteQuestion(chatId, user.getId(), messageText);
                break;
            case "/stop":
                replyText = questionService.handleStop(user.getId());
                break;
            case "/test":
                replyMessage = testService.handleTest(user.getId(), chatId);
                break;
            default:
                replyText = "Неверная команда, для справки используйте /help";
                break;
        }

        return replyMessage == null
                ? new SendMessage(chatId, replyText)
                : replyMessage;
    }
}
