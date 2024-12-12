package org.example.naumenteststgbot.service;

import org.example.naumenteststgbot.entity.TestEntity;
import org.example.naumenteststgbot.states.UserState;
import org.example.naumenteststgbot.util.Util;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * Обработчик всех команд
 */
@Component
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
    private final StateService stateService;
    private final SessionService sessionService;
    private final Util util;

    /**
     * Конструктор класса CommandsHandler
     */
    public CommandsHandler(HelpHandler helpHandler,
                           UserService userService,
                           TestService testService,
                           QuestionService questionService,
                           StateService stateService,
                           SessionService sessionService,
                           Util util) {
        this.helpHandler = helpHandler;
        this.userService = userService;
        this.testService = testService;
        this.questionService = questionService;
        this.stateService = stateService;
        this.sessionService = sessionService;
        this.util = util;
    }

    /**
     * Обрабатывает команду
     *
     * @return сообщение для отправки пользователю
     */
    public String handleCommands(String message, Long userId, String username) {
        String command = message.split(" ")[0];
        String replyText;

        switch (command) {
            case "/start":
                userService.create(userId, username);
                replyText = helpHandler.handle();
                break;
            case "/help":
                replyText = helpHandler.handle();
                break;
            case "/add":
                replyText = handleAdd(userId);
                break;
            case "/view":
                replyText = handleView(userId, message);
                break;
            case "/edit":
                replyText = handleEdit(userId, message);
                break;
            case "/del":
                replyText = handleDel(userId);
                break;
            case "/add_question":
                replyText =questionService.handleAddQuestion(userId, message);
                break;
            case "/view_question":
                replyText = questionService.handleViewQuestions(userId, message);
                break;
            case "/edit_question":
                replyText = questionService.handleEditQuestion(userId, message);
                break;
            case "/del_question":
                replyText = questionService.handleDeleteQuestion(userId,message);
                break;
            case "/stop":
                replyText = questionService.handleStop(userId);
                break;
            default:
                replyText = "Неверная команда, для справки используйте /help";
                break;
        }

        return replyText;
    }


    /**
     * Обработать команду добавления теста
     */
    private String handleAdd(Long userId) {
        TestEntity test = testService.createTest(userId);
        stateService.changeStateById(userId, UserState.ADD_TEST_TITLE);
        sessionService.setCurrentTest(userId, test);
        return "Введите название теста";
    }

    /**
     * Обработать команду просмотра теста
     */
    private String handleView(Long userId, String message) {
        String[] parts = message.split(" ");
        List<TestEntity> tests = testService.getTestsById(userId);

        if (parts.length == 1) {
            stateService.changeStateById(userId, UserState.VIEW_TEST);
            return "Выберите тест для просмотра:\n"
                    + util.testsListToString(tests);
        } else if (util.isNumber(parts[1])){
            stateService.changeStateById(userId, UserState.DEFAULT);
            Long testId = Long.parseLong(parts[1]);
            TestEntity test = testService.getTest(testId);
            if (test == null || !tests.contains(test)) return "Тест не найден!";
            return util.testToString(test);
        }
        return "Ошибка ввода!";
    }

    /**
     * Обработать команду редактирования теста
     */
    private String handleEdit(Long userId, String message) {
        String[] parts = message.split(" ");
        List<TestEntity> tests = testService.getTestsById(userId);
        if (parts.length == 1)
            return "Используйте команду вместе с идентификатором теста!";
        else if (!util.isNumber(parts[1]))
            return "Ошибка ввода!";
        Long testId = Long.parseLong(parts[1]);
        TestEntity test = testService.getTest(testId);
        if (test == null || !tests.contains(test))
            return "Тест не найден!";
        sessionService.setCurrentTest(userId, test);
        stateService.changeStateById(userId, UserState.EDIT_TEST);
        return String.format("""
                Вы выбрали тест “%s”. Что вы хотите изменить?
                1: Название теста
                2: Описание теста
                """, test.getTitle());
    }

    /**
     * Обработать команду удаления теста
     */
    private String handleDel(Long id) {
        stateService.changeStateById(id, UserState.DELETE_TEST);
        return "Выберите тест:\n"
                + util.testsListToString(testService.getTestsById(id));
    }
}
