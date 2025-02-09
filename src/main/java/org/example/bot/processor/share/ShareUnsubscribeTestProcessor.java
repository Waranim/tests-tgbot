package org.example.bot.processor.share;

import org.example.bot.entity.TestEntity;
import org.example.bot.entity.UserEntity;
import org.example.bot.processor.AbstractCallbackProcessor;
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
    /**
     * Сервис для работы с пользователями
     */
    private final UserService userService;

    /**
     * Сервис для работы с тестами
     */
    private final TestService testService;

    /**
     * Конструктор для инициализации обработчика callback.
     */
    public ShareUnsubscribeTestProcessor(UserService userService, TestService testService) {
        super("SHARE_UNSUBSCRIBE_TEST");
        this.userService = userService;
        this.testService = testService;
    }

    @Override
    public BotResponse process(Long userId, String message) {
        Long testId = Long.parseLong(message.split(" ")[1]);
        List<TestEntity> receivedTests = userService.getOpenReceivedTests(userId);
        Optional<TestEntity> testOpt = testService.getTest(testId);
        if(testOpt.isEmpty() || !receivedTests.contains(testOpt.get()))
            return new BotResponse("У вас нет доступа к тесту");
        TestEntity test = testOpt.get();

        Optional<UserEntity> userOpt = userService.getUserById(userId);
        if (userOpt.isEmpty())
            return new BotResponse("Пользователь не найден");

        UserEntity user = userOpt.get();
        userService.removeReceivedTest(userId, test);
        test.getRecipients().remove(user);
        testService.update(test);
        String creatorUsername = userService.getUserById(test.getCreatorId()).get().getUsername();
        return new BotResponse(
                ("Вы отписались от теста “%s (%s)”. " +
                        "Чтобы вернуть доступ к тесту необходимо обратится к его владельцу.")
                        .formatted(test.getTitle(), creatorUsername));
    }
}
