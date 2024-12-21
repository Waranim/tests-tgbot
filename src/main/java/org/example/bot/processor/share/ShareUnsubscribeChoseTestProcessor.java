package org.example.bot.processor.share;

import org.example.bot.dto.InlineButtonDTO;
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
 * Обработать выбор теста при команде /shared_tests
 */
@Component
public class ShareUnsubscribeChoseTestProcessor extends AbstractCallbackProcessor {
    /**
     * Сервис для работы с пользователями.
     */
    private final UserService userService;

    /**
     * Сервис для работы с тестами.
     */
    private final TestService testService;

    /**
     * Конструктор для инициализации обработчика callback.
     */
    public ShareUnsubscribeChoseTestProcessor(UserService userService, TestService testService) {
        super("SHARE_UNSUBSCRIBE_CHOOSE_TEST");
        this.userService = userService;
        this.testService = testService;
    }

    @Override
    public BotResponse process(Long userId, String callback) {
        Long testId = Long.parseLong(callback.split(" ")[1]);
        List<TestEntity> receivedTests = userService.getOpenReceivedTests(userId);
        Optional<TestEntity> testOpt = testService.getTest(testId);
        if(testOpt.isEmpty() || !receivedTests.contains(testOpt.get()))
            return new BotResponse("У вас нет доступа к тесту");
        TestEntity test = testOpt.get();

        List<List<InlineButtonDTO>> button = List.of(
                List.of(new InlineButtonDTO(
                        "Отписаться",
                        "SHARE_UNSUBSCRIBE_TEST " + testId)));

        String creatorUsername = userService.getUserById(test.getCreatorId())
                .map(UserEntity::getUsername)
                .orElse("неизвестный пользователь");

        return new BotResponse(
                "Вы выбрали “%s (%s)”. Всего вопросов: %s."
                        .formatted(test.getTitle(),
                                creatorUsername,
                                test.getQuestions().size()),
                button,
                false);
    }
}
