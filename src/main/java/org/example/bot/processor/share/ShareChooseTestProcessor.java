package org.example.bot.processor.share;

import org.example.bot.entity.TestEntity;
import org.example.bot.processor.AbstractCallbackProcessor;
import org.example.bot.service.ContextService;
import org.example.bot.service.StateService;
import org.example.bot.service.TestService;
import org.example.bot.state.UserState;
import org.example.bot.telegram.BotResponse;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Обработать выбор теста при команде поделиться
 */
@Component
public class ShareChooseTestProcessor extends AbstractCallbackProcessor {

    /**
     * Сервис для управления тестами.
     */
    private final TestService testService;

    /**
     * Сервис для управления контекстом
     */
    private final ContextService contextService;

    /**
     * Сервис для управления состояниями
     */
    private final StateService stateService;

    /**
     * Конструктор для инициализации обработчика callback.
     */
    public ShareChooseTestProcessor(TestService testService, ContextService contextService, StateService stateService) {
        super("SHARE_CHOOSE_TEST");
        this.testService = testService;
        this.contextService = contextService;
        this.stateService = stateService;
    }

    @Override
    public BotResponse process(Long userId, String callback) {
        Long testId = Long.parseLong(callback.split(" ")[1]);
        Optional<TestEntity> currentTestOpt = testService.getTest(testId);
        if (currentTestOpt.isEmpty())
            return new BotResponse("Тест не найден");
        TestEntity currentTest = currentTestOpt.get();
        if(!currentTest.isAccessOpen())
            return new BotResponse("У теста закрыт доступ!");
        contextService.setCurrentTest(userId, currentTest);
        stateService.changeStateById(userId, UserState.CHOOSE_USER);

        return new BotResponse("Введите идентификатор пользователя (его можно посмотреть командой /info)");
    }
}
