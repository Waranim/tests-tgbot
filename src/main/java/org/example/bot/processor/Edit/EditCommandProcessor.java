package org.example.bot.processor.Edit;

import org.example.bot.entity.TestEntity;
import org.example.bot.processor.AbstractCommandProcessor;
import org.example.bot.service.ContextService;
import org.example.bot.service.StateService;
import org.example.bot.service.TestService;
import org.example.bot.state.UserState;
import org.example.bot.util.Util;
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
     * Сервис для управления контекстом.
     */
    private final ContextService contextService;
    
    /**
     * Сервис для управления состояниями.
     */
    private final StateService stateService;

    /**
     * Конструктор для инициализации обработчика команды редактирования теста.
     * 
     * @param testService сервис для управления тестами
     * @param util утилита с вспомогательными методами
     * @param contextService сервис для управления контекстом
     * @param stateService сервис для управления состояниями
     */
    public EditCommandProcessor(TestService testService,
                                Util util,
                                ContextService contextService,
                                StateService stateService) {
        super("/edit");
        this.testService = testService;
        this.util = util;
        this.contextService = contextService;
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
        contextService.setCurrentTest(userId, test);
        stateService.changeStateById(userId, UserState.EDIT_TEST);
        return String.format("""
                Вы выбрали тест “%s”. Что вы хотите изменить?
                1: Название теста
                2: Описание теста
                """, test.getTitle());
    }
}
