package org.example.bot.processor.Edit;

import org.example.bot.entity.TestEntity;
import org.example.bot.processor.AbstractStateProcessor;
import org.example.bot.service.ContextService;
import org.example.bot.service.StateService;
import org.example.bot.service.TestService;
import org.example.bot.state.UserState;
import org.springframework.stereotype.Component;

/**
 * Обработчик состояния редактирования названия теста.
 */
@Component
public class EditTestTitleProcessor extends AbstractStateProcessor {
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
     * Конструктор для инициализации обработчика редактирования названия теста.
     * 
     * @param stateService сервис для управления состояниями
     * @param contextService сервис для управления контекстом
     * @param testService сервис для управления тестами
     */
    public EditTestTitleProcessor(StateService stateService,
                                  ContextService contextService,
                                  TestService testService) {
        super(stateService, UserState.EDIT_TEST_TITLE);
        this.stateService = stateService;
        this.contextService = contextService;
        this.testService = testService;
    }

    @Override
    public String process(Long userId, String message) {
        TestEntity currentTest = contextService.getCurrentTest(userId);
        currentTest.setTitle(message);
        testService.update(currentTest);
        stateService.changeStateById(userId, UserState.DEFAULT);

        return String.format("Название изменено на “%s”", message);
    }
}
