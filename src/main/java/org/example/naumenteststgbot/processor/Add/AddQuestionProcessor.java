package org.example.naumenteststgbot.processor.Add;

import org.example.naumenteststgbot.entity.QuestionEntity;
import org.example.naumenteststgbot.entity.TestEntity;
import org.example.naumenteststgbot.processor.AbstractStateProcessor;
import org.example.naumenteststgbot.service.QuestionService;
import org.example.naumenteststgbot.service.SessionService;
import org.example.naumenteststgbot.service.StateService;
import org.example.naumenteststgbot.service.TestService;
import org.example.naumenteststgbot.states.UserState;
import org.example.naumenteststgbot.util.Util;
import org.springframework.stereotype.Component;

/**
 * Обработчик состояния добавления вопроса
 */
@Component
public class AddQuestionProcessor extends AbstractStateProcessor {

    /**
     * Сервис для управления сессиями.
     */
    private final SessionService sessionService;

    /**
     * Сервис для управления вопросами.
     */
    private final QuestionService questionService;

    /**
     * Утилита с вспомогательными методами
     */
    private final Util util;

    /**
     * Сервис для управления тестами
     */
    private final TestService testService;

    /**
     * Конструктор для инициализации обработчика добавления вопроса
     *
     * @param stateService    сервис для управления состояниями
     * @param sessionService  сервис для управления сессиями
     * @param questionService сервис для управления тестами
     * @param testService сервис для управления тестами
     * @param util утилита с вспомогательными методами
     */
    public AddQuestionProcessor(StateService stateService,
                                SessionService sessionService,
                                QuestionService questionService,
                                Util util, TestService testService) {
        super(stateService, UserState.ADD_QUESTION);
        this.sessionService = sessionService;
        this.questionService = questionService;
        this.util = util;
        this.testService = testService;
    }

    @Override
    public String process(Long userId, String message) {
        if (!util.isNumber(message)) {
            return "Некорректный id теста. Пожалуйста, введите число.";
        }

        long testId = Long.parseLong(message);
        TestEntity selectedTest = testService.getTest(testId);
        if (selectedTest == null || !testService.getTestsByUserId(userId).contains(selectedTest)) {
            return "Тест не найден!";
        }
        QuestionEntity nquestion = questionService.createQuestion(selectedTest);
        sessionService.setCurrentQuestion(userId, nquestion);
        stateService.changeStateById(userId, UserState.ADD_QUESTION_TEXT);
        return String.format("Введите название вопроса для теста “%s”", selectedTest.getTitle());
    }
}
