package org.example.bot.processor.Edit;

import org.example.bot.entity.TestEntity;
import org.example.bot.processor.AbstractStateProcessor;
import org.example.bot.service.SessionService;
import org.example.bot.service.StateService;
import org.example.bot.service.TestService;
import org.example.bot.states.UserState;
import org.springframework.stereotype.Component;

/**
 * Обработчик состояния редактирования описания теста.
 */
@Component
public class EditTestDescriptionProcessor extends AbstractStateProcessor {
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
     * Конструктор для инициализации обработчика редактирования описания теста.
     * 
     * @param stateService сервис для управления состояниями
     * @param sessionService сервис для управления сессиями
     * @param testService сервис для управления тестами
     */
    public EditTestDescriptionProcessor(StateService stateService,
                                        SessionService sessionService,
                                        TestService testService) {
        super(stateService, UserState.EDIT_TEST_DESCRIPTION);
        this.stateService = stateService;
        this.sessionService = sessionService;
        this.testService = testService;
    }


    @Override
    public String process(Long userId, String message) {
        TestEntity currentTest = sessionService.getCurrentTest(userId);
        currentTest.setDescription(message);
        testService.update(currentTest);
        stateService.changeStateById(userId, UserState.DEFAULT);
        return String.format("Описание изменено на “%s”", message);
    }
}
