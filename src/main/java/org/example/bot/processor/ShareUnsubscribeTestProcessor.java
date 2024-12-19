package org.example.bot.processor;

import org.example.bot.entity.TestEntity;
import org.example.bot.service.TestService;
import org.example.bot.service.UserService;
import org.example.bot.telegram.BotResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Обработать отписку при команде /shared_tests
 */
@Component
public class ShareUnsubscribeTestProcessor extends AbstractCallbackProcessor {
    private final UserService userService;
    private final TestService testService;

    /**
     * Конструктор для инициализации обработчика callback.
     */
    protected ShareUnsubscribeTestProcessor(UserService userService, TestService testService) {
        super("SHARE_UNSUBSCRIBE_TEST");
        this.userService = userService;
        this.testService = testService;
    }

    @Override
    public BotResponse process(Long userId, String message) {
        Long testId = Long.parseLong(extractData(message));
        List<TestEntity> receivedTests = userService.getOpenReceivedTests(userId);
        Optional<TestEntity> testOpt = testService.getTest(testId);
        if(testOpt.isEmpty() || !receivedTests.contains(testOpt.get()))
            return new BotResponse("У вас нет доступа к тесту");
        TestEntity test = testOpt.get();

        userService.removeReceivedTest(userId, test);
        test.getRecipients().remove(userService.getUserById(userId));
        String creatorUsername = userService.getUserById(test.getCreatorId()).get().getUsername();
        return new BotResponse(
                ("Вы отписались от теста “%s (%s)” " +
                        "Чтобы вернуть доступ к тесту необходимо обратится к его владельцу.")
                        .formatted(test.getTitle(), creatorUsername));
    }
}
