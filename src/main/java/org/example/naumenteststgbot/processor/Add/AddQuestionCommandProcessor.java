package org.example.naumenteststgbot.processor.Add;

import org.example.naumenteststgbot.entity.QuestionEntity;
import org.example.naumenteststgbot.entity.TestEntity;
import org.example.naumenteststgbot.processor.AbstractCommandProcessor;
import org.example.naumenteststgbot.service.QuestionService;
import org.example.naumenteststgbot.service.SessionService;
import org.example.naumenteststgbot.service.StateService;
import org.example.naumenteststgbot.service.TestService;
import org.example.naumenteststgbot.states.UserState;
import org.example.naumenteststgbot.util.Util;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Обработчик команды добавления вопроса в тест
 */
@Component
public class AddQuestionCommandProcessor extends AbstractCommandProcessor {

    /**
     * Сервис для управления тестами
     */
    private final TestService testService;

    /**
     * Сервис для управления вопросами
     */
    private final QuestionService questionService;

    /**
     * Сервис для управления состояниями
     */
    private final StateService stateService;

    /**
     * Сервис для управления сессиями
     */
    private final SessionService sessionService;

    /**
     * Утилита с вспомогательными методами
     */
    private final Util util;

    /**
     * Конструктор для инициализации обработчика команды добавления вопроса в тест
     *
     * @param testService     сервис для управления тестами
     * @param questionService сервис для управления вопросами
     * @param stateService    сервис для управления состояниями
     * @param sessionService  сервис для управления сессиями
     * @param util            утилита с вспомогательными методами
     */
    public AddQuestionCommandProcessor(TestService testService,
                                       QuestionService questionService,
                                       StateService stateService,
                                       SessionService sessionService,
                                       Util util) {
        super("/add_question");
        this.testService = testService;
        this.questionService = questionService;
        this.stateService = stateService;
        this.sessionService = sessionService;
        this.util = util;
    }

    @Override
    public String process(Long userId, String message) {
        String[] parts = message.split(" ");
        List<TestEntity> tests = testService.getTestsByUserId(userId);
        if (parts.length == 1) {
            if (tests.isEmpty()) {
                return "У вас нет доступных тестов для добавления вопросов.";
            }
            stateService.changeStateById(userId, UserState.ADD_QUESTION);
            return "Выберите тест:\n" + util.testsListToString(tests);
        }
        String testIdStr = parts[1];
        if (!util.isNumber(testIdStr)) {
            return "Ошибка ввода. Укажите корректный id теста.";
        }
        long testId = Long.parseLong(testIdStr);
        TestEntity test = testService.getTest(testId);
        if (test == null || !tests.contains(test)) {
            return "Тест не найден!";
        }
        QuestionEntity question = questionService.createQuestion(test);
        stateService.changeStateById(userId, UserState.ADD_QUESTION_TEXT);
        sessionService.setCurrentQuestion(userId, question);
        return String.format("Введите название вопроса для теста “%s”", test.getTitle());
    }
}
