package org.example.bot.processor.Del;

import org.example.bot.processor.AbstractCommandProcessor;
import org.example.bot.service.StateService;
import org.example.bot.service.TestService;
import org.example.bot.state.UserState;
import org.example.bot.telegram.BotResponse;
import org.example.bot.util.TestUtils;
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
     * Утилита с вспомогательными методами для тестов.
     */
    private final TestUtils testUtils;

    /**
     * Конструктор для инициализации обработчика команды удаления теста.
     * 
     * @param stateService сервис для управления состояниями
     * @param testService сервис для управления тестами
     * @param testUtils утилита с вспомогательными методами для тестов
     */
    public DelCommandProcessor(StateService stateService,
                               TestService testService,
                               TestUtils testUtils) {
        super("/del");
        this.stateService = stateService;
        this.testService = testService;
        this.testUtils = testUtils;
    }


    @Override
    public BotResponse process(Long userId, String message) {
        stateService.changeStateById(userId, UserState.DELETE_TEST);
        return new BotResponse("Выберите тест:\n"
                + testUtils.testsToString(testService.getTestsByUserId(userId)));
    }
}
