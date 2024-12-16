package org.example.bot.processor.Del;

import org.example.bot.entity.TestEntity;
import org.example.bot.processor.AbstractStateProcessor;
import org.example.bot.service.SessionService;
import org.example.bot.service.StateService;
import org.example.bot.service.TestService;
import org.example.bot.states.UserState;
import org.example.bot.util.Util;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Обработчик состояния удаления теста.
 */
@Component
public class DelTestProcessor extends AbstractStateProcessor {
    /**
     * Сервис для управления состояниями.
     */
    private final StateService stateService;
    
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
     * Конструктор для инициализации обработчика удаления теста.
     * 
     * @param stateService сервис для управления состояниями
     * @param testService сервис для управления тестами
     * @param util утилита с вспомогательными методами
     * @param sessionService сервис для управления сессиями
     */
    public DelTestProcessor(StateService stateService,
                            TestService testService,
                            Util util,
                            SessionService sessionService) {
        super(stateService, UserState.DELETE_TEST);
        this.stateService = stateService;
        this.testService = testService;
        this.util = util;
        this.sessionService = sessionService;
    }

    @Override
    public String process(Long userId, String message) {
        if(!util.isNumber(message))
            return  "Введите число!";

        TestEntity test = testService.getTest(Long.parseLong(message));
        List<TestEntity> tests = testService.getTestsByUserId(userId);
        if (test == null || !tests.contains(test))
            return "Тест не найден!";

        sessionService.setCurrentTest(userId, test);
        stateService.changeStateById(userId, UserState.CONFIRM_DELETE_TEST);
        return String.format("Тест “%s” будет удалён, вы уверены? (Да/Нет)", test.getTitle());
    }
}
