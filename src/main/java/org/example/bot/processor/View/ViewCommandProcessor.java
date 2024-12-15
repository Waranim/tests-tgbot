package org.example.bot.processor.View;

import org.example.bot.entity.TestEntity;
import org.example.bot.processor.AbstractCommandProcessor;
import org.example.bot.service.StateService;
import org.example.bot.service.TestService;
import org.example.bot.states.UserState;
import org.example.bot.util.Util;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * Обработчик команды просмотра тестов.
 */
@Component
public class ViewCommandProcessor extends AbstractCommandProcessor {
    /**
     * Сервис для управления тестами.
     */
    private final TestService testService;
    
    /**
     * Сервис для управления состояниями.
     */
    private final StateService stateService;
    
    /**
     * Утилита с вспомогательными методами.
     */
    private final Util util;

    /**
     * Конструктор для инициализации обработчика команды просмотра тестов.
     * 
     * @param testService сервис для управления тестами
     * @param stateService сервис для управления состояниями
     * @param util утилита с вспомогательными методами
     */
    public ViewCommandProcessor(TestService testService, StateService stateService, Util util) {
        super("/view");
        this.testService = testService;
        this.stateService = stateService;
        this.util = util;
    }

    @Override
    public String process(Long userId, String message) {
        String[] parts = message.split(" ");
        List<TestEntity> tests = testService.getTestsByUserId(userId);

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
}
