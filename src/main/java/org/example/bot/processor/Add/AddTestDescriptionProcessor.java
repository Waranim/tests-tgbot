package org.example.bot.processor.Add;

import org.example.bot.entity.TestEntity;
import org.example.bot.processor.AbstractStateProcessor;
import org.example.bot.service.SessionService;
import org.example.bot.service.StateService;
import org.example.bot.service.TestService;
import org.example.bot.states.UserState;
import org.springframework.stereotype.Component;

/**
 * Обработчик состояния добавления описания теста.
 */
@Component
public class AddTestDescriptionProcessor extends AbstractStateProcessor {
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
     * Конструктор для инициализации обработчика добавления описания теста.
     * 
     * @param stateService сервис для управления состояниями
     * @param sessionService сервис для управления сессиями
     * @param testService сервис для управления тестами
     */
    public AddTestDescriptionProcessor(StateService stateService,
                                       SessionService sessionService,
                                       TestService testService) {
        super(stateService, UserState.ADD_TEST_DESCRIPTION);
        this.stateService = stateService;
        this.sessionService = sessionService;
        this.testService = testService;
    }

    @Override
    public String process(Long userId, String message) {
        TestEntity currentTest = sessionService.getCurrentTest(userId);
        currentTest.setDescription(message);
        stateService.changeStateById(userId, UserState.DEFAULT);
        testService.update(currentTest);
        return String.format("Тест “%s” создан! Количество вопросов: 0. " +
                        "Для добавление вопросов используйте /add_question %s, где %s - идентификатор теста “%s”.",
                currentTest.getTitle(), currentTest.getId(), currentTest.getId(), currentTest.getTitle());
    }
}
