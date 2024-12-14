package org.example.naumenteststgbot.processor.Edit;

import org.example.naumenteststgbot.entity.TestEntity;
import org.example.naumenteststgbot.processor.AbstractCommandProcessor;
import org.example.naumenteststgbot.service.SessionService;
import org.example.naumenteststgbot.service.StateService;
import org.example.naumenteststgbot.service.TestService;
import org.example.naumenteststgbot.states.UserState;
import org.example.naumenteststgbot.util.Util;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Обработчик команды редактирования теста.
 */
@Component
public class EditCommandProcessor extends AbstractCommandProcessor {
    /**
     * Сервис для управления тестами.
     */
    private final TestService testService;
    
    /**
     * Утилита с вспомогательными методами.
     */
    private final Util util;
    
    /**
     * Сервис для управления сессиями.
     */
    private final SessionService sessionService;
    
    /**
     * Сервис для управления состояниями.
     */
    private final StateService stateService;

    /**
     * Конструктор для инициализации обработчика команды редактирования теста.
     * 
     * @param testService сервис для управления тестами
     * @param util утилита с вспомогательными методами
     * @param sessionService сервис для управления сессиями
     * @param stateService сервис для управления состояниями
     */
    public EditCommandProcessor(TestService testService,
                                Util util,
                                SessionService sessionService,
                                StateService stateService) {
        super("/edit");
        this.testService = testService;
        this.util = util;
        this.sessionService = sessionService;
        this.stateService = stateService;
    }

    @Override
    public String process(Long userId, String message) {
        String[] parts = message.split(" ");
        List<TestEntity> tests = testService.getTestsByUserId(userId);
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
}
