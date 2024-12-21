package org.example.bot.processor.share;

import org.example.bot.entity.TestEntity;
import org.example.bot.entity.UserEntity;
import org.example.bot.processor.AbstractStateProcessor;
import org.example.bot.service.ContextService;
import org.example.bot.service.StateService;
import org.example.bot.service.TestService;
import org.example.bot.service.UserService;
import org.example.bot.state.UserState;
import org.example.bot.telegram.BotResponse;
import org.example.bot.util.NumberUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Обработать выбор пользователя при команде поделиться
 */
@Component
public class ShareChooseUserProcessor extends AbstractStateProcessor {
    /**
     * Утилита с вспомогательными методами
     */
    private final NumberUtils numberUtils;

    /**
     * Сервис для управления пользователями
     */
    private final UserService userService;

    /**
     * Сервис для управления контекстом
     */
    private final ContextService contextService;

    /**
     * Сервис для управления тестами
     */
    private final TestService testService;

    /**
     * Конструктор для инициализации обработчика состояния.
     *
     * @param stateService  сервис для управления состояниями
     */
    public ShareChooseUserProcessor(StateService stateService, NumberUtils numberUtils, UserService userService, ContextService contextService, TestService testService) {
        super(stateService, UserState.CHOOSE_USER);
        this.numberUtils = numberUtils;
        this.userService = userService;
        this.contextService = contextService;
        this.testService = testService;
    }

    @Override
    public BotResponse process(Long userId, String message) {
        if(!numberUtils.isNumber(message))
            return new BotResponse("Некорректный id пользователя");
        Long recipientId = Long.parseLong(message);
        Optional<UserEntity> recipientUserOpt = userService.getUserById(recipientId);
        if(recipientUserOpt.isEmpty())
            return new BotResponse("Пользователь не найден");
        UserEntity recipientUser = recipientUserOpt.get();
        Optional<TestEntity> testOpt = contextService.getCurrentTest(userId);
        if(testOpt.isEmpty())
            return new BotResponse("Тест не найден");
        TestEntity test = testOpt.get();
        List<TestEntity> receivedTests = recipientUser.getReceivedTests();
        if(receivedTests.contains(test) || recipientUser.getTests().contains(test))
            return new BotResponse("Пользователь уже имеет доступ к этому тесту");

        test.getRecipients().add(recipientUser);
        userService.addReceivedTest(recipientId, test);
        stateService.changeStateById(userId, UserState.DEFAULT);
        testService.update(test);
        return new BotResponse("Пользователь " + message + " получил доступ к тесту");
    }
}
