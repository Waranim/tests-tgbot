package org.example.bot.processor.Del;

import org.example.bot.entity.TestEntity;
import org.example.bot.processor.AbstractStateProcessor;
import org.example.bot.service.ContextService;
import org.example.bot.service.StateService;
import org.example.bot.service.TestService;
import org.example.bot.state.UserState;
import org.springframework.stereotype.Component;

/**
 * Обработчик состояния подтверждения удаления теста.
 */
@Component
public class ConfirmDelTest extends AbstractStateProcessor {
    /**
     * Сервис для управления состояниями.
     */
    private final StateService stateService;
    
    /**
     * Сервис для управления контекстом.
     */
    private final ContextService contextService;
    
    /**
     * Сервис для управления тестами.
     */
    private final TestService testService;

    /**
     * Конструктор для инициализации обработчика подтверждения удаления теста.
     * 
     * @param stateService сервис для управления состояниями
     * @param contextService сервис для управления контекстом
     * @param testService сервис для управления тестами
     */
    public ConfirmDelTest(StateService stateService,
                          ContextService contextService,
                          TestService testService) {
        super(stateService, UserState.CONFIRM_DELETE_TEST);
        this.stateService = stateService;
        this.contextService = contextService;
        this.testService = testService;
    }

    @Override
    public String process(Long userId, String message) {
        message = message.toLowerCase();
        TestEntity currentTest = contextService.getCurrentTest(userId);
        stateService.changeStateById(userId, UserState.DEFAULT);
        if (!message.equals("да"))
            return String.format("Тест “%s” не удалён", currentTest.getTitle());
        contextService.setCurrentTest(userId, null);
        contextService.setCurrentQuestion(userId, null);
        testService.delete(currentTest);

        return String.format("Тест “%s” удалён", currentTest.getTitle());
    }
}
