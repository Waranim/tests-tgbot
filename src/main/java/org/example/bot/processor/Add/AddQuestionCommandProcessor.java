package org.example.bot.processor.Add;

import org.example.bot.entity.QuestionEntity;
import org.example.bot.entity.TestEntity;
import org.example.bot.processor.AbstractCommandProcessor;
import org.example.bot.service.QuestionService;
import org.example.bot.service.ContextService;
import org.example.bot.service.StateService;
import org.example.bot.service.TestService;
import org.example.bot.state.UserState;
import org.example.bot.util.Util;
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
     * Сервис для управления контекстом
     */
    private final ContextService contextService;

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
     * @param contextService  сервис для управления контекстом
     * @param util            утилита с вспомогательными методами
     */
    public AddQuestionCommandProcessor(TestService testService,
                                       QuestionService questionService,
                                       StateService stateService,
                                       ContextService contextService,
                                       Util util) {
        super("/add_question");
        this.testService = testService;
        this.questionService = questionService;
        this.stateService = stateService;
        this.contextService = contextService;
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
        contextService.setCurrentQuestion(userId, question);
        return String.format("Введите название вопроса для теста “%s”", test.getTitle());
    }
}
