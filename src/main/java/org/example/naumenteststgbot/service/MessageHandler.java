package org.example.naumenteststgbot.service;

import org.example.naumenteststgbot.entity.TestEntity;
import org.example.naumenteststgbot.entity.UserSession;
import org.example.naumenteststgbot.states.UserState;
import org.example.naumenteststgbot.util.Util;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * Обработчик сообщений
 */
@Component
public class MessageHandler {
    /**
     * Сервис для взаимодействия с тестами
     */
    private final TestService testService;

    /**
     * Сервис для взаимодействия с вопросами
     */
    private final QuestionService questionService;
    private final SessionService sessionService;
    private final StateService stateService;
    private final Util util;
    private final CommandsHandler commandsHandler;

    /**
     * Конструктор класса MessageHandler
     */
    public MessageHandler(UserService userService, TestService testService, QuestionService questionService, SessionService sessionService, StateService stateService, Util util, CommandsHandler commandsHandler) {
        this.userService = userService;
        this.testService = testService;
        this.questionService = questionService;
        this.sessionService = sessionService;
        this.stateService = stateService;
        this.util = util;
        this.commandsHandler = commandsHandler;
    }

    /**
     * Обработать сообщение
     */
    public String handleMessage(String message, Long userId) {
        UserSession userSession = sessionService.getSession(userId);
        UserState userState = stateService.getCurrentState(userId);
        String responseMessage = "Я вас не понимаю, для справки используйте /help";
        TestEntity currentTest = userSession.getCurrentTest();
        switch (userState) {
            case DEFAULT:
                break;
            case ADD_TEST_TITLE:
                currentTest.setTitle(message);
                responseMessage = "Введите описание теста";
                stateService.changeStateById(userId, UserState.ADD_TEST_DESCRIPTION);
                break;
            case ADD_TEST_DESCRIPTION:
                currentTest.setDescription(message);
                responseMessage = String.format("Тест “%s” создан! Количество вопросов: 0. Для добавление вопросов используйте /add_question %s, где %s - идентификатор теста “%s”.", currentTest.getTitle(), currentTest.getId(), currentTest.getId(), currentTest.getTitle());
                stateService.changeStateById(userId, UserState.DEFAULT);
                break;
            case EDIT_TEST:
                if(message.equals("1")){
                    responseMessage = "Введите новое название теста";
                    stateService.changeStateById(userId, UserState.EDIT_TEST_TITLE);
                }
                else if(message.equals("2")){
                    responseMessage = "Введите новое описание теста";
                    stateService.changeStateById(userId, UserState.EDIT_TEST_DESCRIPTION);
                }
                break;
            case EDIT_TEST_TITLE:
                currentTest.setTitle(message);
                stateService.changeStateById(userId, UserState.DEFAULT);
                responseMessage = String.format("Название изменено на “%s”", message);
                break;
            case EDIT_TEST_DESCRIPTION:
                currentTest.setDescription(message);
                stateService.changeStateById(userId, UserState.DEFAULT);
                responseMessage = String.format("Описание изменено на “%s”", message);
                break;
            case DELETE_TEST:
                if(!util.isNumber(message)) {
                    responseMessage = "Ошибка ввода!";
                    break;
                }
                TestEntity test = testService.getTest(Long.parseLong(message));
                List<TestEntity> tests = testService.getTestsById(userId);
                if (test == null || !tests.contains(test)) return "Тест не найден!";
                responseMessage = String.format("Тест “%s” будет удалён, вы уверены? (Да/Нет)", test.getTitle());
                sessionService.setCurrentTest(userId, test);
                stateService.changeStateById(userId, UserState.CONFIRM_DELETE_TEST);
                break;
            case CONFIRM_DELETE_TEST:
                message = message.toLowerCase();
                stateService.changeStateById(userId, UserState.DEFAULT);
                if (message.equals("да"))
                {
                    sessionService.setCurrentTest(userId, null);
                    sessionService.setCurrentQuestion(userId, null);
                    testService.delete(currentTest);
                    return String.format("Тест “%s” удалён", currentTest.getTitle());
                }
                else{
                    return String.format("Тест “%s” не удалён", currentTest.getTitle());
                }
            case VIEW_TEST:
                return commandsHandler.handleCommands("/view " + message, userId, null);


            case ADD_QUESTION_TEXT:
            case ADD_QUESTION:
            case ADD_ANSWER:
            case SET_CORRECT_ANSWER:
            case EDIT_QUESTION:
            case EDIT_QUESTION_TEXT:
            case EDIT_ANSWER_OPTION_CHOICE:
            case EDIT_ANSWER_TEXT_CHOICE:
            case EDIT_ANSWER_TEXT:
            case DELETE_QUESTION:
            case CONFIRM_DELETE_QUESTION:
                responseMessage = questionService.handleMessage(userSession, message);
                break;

            default:
                break;
        }

        testService.update(currentTest);

        return responseMessage;
    }
}
