package org.example.bot.processor.Del;

import org.example.bot.processor.AbstractCommandProcessor;
import org.example.bot.service.StateService;
import org.example.bot.service.TestService;
import org.example.bot.state.UserState;
import org.example.bot.util.Util;
import org.springframework.stereotype.Component;

/**
 * Обработчик команды удаления теста.
 */
@Component
public class DelCommandProcessor extends AbstractCommandProcessor {
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
     * Конструктор для инициализации обработчика команды удаления теста.
     * 
     * @param stateService сервис для управления состояниями
     * @param testService сервис для управления тестами
     * @param util утилита с вспомогательными методами
     */
    public DelCommandProcessor(StateService stateService,
                               TestService testService,
                               Util util) {
        super("/del");
        this.stateService = stateService;
        this.testService = testService;
        this.util = util;
    }


    @Override
    public String process(Long userId, String message) {
        stateService.changeStateById(userId, UserState.DELETE_TEST);
        return "Выберите тест:\n"
                + util.testsListToString(testService.getTestsByUserId(userId));
    }
}
