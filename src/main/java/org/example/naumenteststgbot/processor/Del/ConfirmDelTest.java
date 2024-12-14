package org.example.naumenteststgbot.processor.Del;

import org.example.naumenteststgbot.entity.TestEntity;
import org.example.naumenteststgbot.processor.AbstractStateProcessor;
import org.example.naumenteststgbot.service.SessionService;
import org.example.naumenteststgbot.service.StateService;
import org.example.naumenteststgbot.service.TestService;
import org.example.naumenteststgbot.states.UserState;
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
     * Сервис для управления сессиями.
     */
    private final SessionService sessionService;
    
    /**
     * Сервис для управления тестами.
     */
    private final TestService testService;

    /**
     * Конструктор для инициализации обработчика подтверждения удаления теста.
     * 
     * @param stateService сервис для управления состояниями
     * @param sessionService сервис для управления сессиями
     * @param testService сервис для управления тестами
     */
    public ConfirmDelTest(StateService stateService,
                          SessionService sessionService,
                          TestService testService) {
        super(stateService, UserState.CONFIRM_DELETE_TEST);
        this.stateService = stateService;
        this.sessionService = sessionService;
        this.testService = testService;
    }

    @Override
    public String process(Long userId, String message) {
        message = message.toLowerCase();
        TestEntity currentTest = sessionService.getCurrentTest(userId);
        stateService.changeStateById(userId, UserState.DEFAULT);
        if (!message.equals("да"))
            return String.format("Тест “%s” не удалён", currentTest.getTitle());
        sessionService.setCurrentTest(userId, null);
        sessionService.setCurrentQuestion(userId, null);
        testService.delete(currentTest);

        return String.format("Тест “%s” удалён", currentTest.getTitle());
    }
}
