package org.example.bot.processor;

import org.example.bot.entity.TestEntity;
import org.example.bot.service.TestService;
import org.example.bot.service.UserService;
import org.example.bot.telegram.BotResponse;
import org.example.bot.util.ButtonUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;
import java.util.Optional;

/**
 * Обработать выбор теста при команде /shared_tests
 */
@Component
public class ShareUnsubscribeChoseTestProcessor extends AbstractCallbackProcessor {
    private final UserService userService;
    private final TestService testService;
    private final ButtonUtils buttonUtils;

    /**
     * Конструктор для инициализации обработчика callback.
     */
    protected ShareUnsubscribeChoseTestProcessor(UserService userService, TestService testService, ButtonUtils buttonUtils) {
        super("SHARE_UNSUBSCRIBE_CHOOSE_TEST");
        this.userService = userService;
        this.testService = testService;
        this.buttonUtils = buttonUtils;
    }

    @Override
    public BotResponse process(Long userId, String message) {
        Long testId = Long.parseLong(extractData(message));
        List<TestEntity> receivedTests = userService.getOpenReceivedTests(userId);
        Optional<TestEntity> testOpt = testService.getTest(testId);
        if(testOpt.isEmpty() || !receivedTests.contains(testOpt.get()))
            return new BotResponse("У вас нет доступа к тесту");
        TestEntity test = testOpt.get();

        InlineKeyboardButton button = buttonUtils.createButton("Отписаться"
                , "SHARE_UNSUBSCRIBE_TEST " + testId);
        String creatorUsername = userService.getUserById(test.getCreatorId()).get().getUsername();
        return new BotResponse(
                "Вы выбрали “%s (%s)”. Всего вопросов: %s."
                        .formatted(test.getTitle(), creatorUsername, test.getQuestions().size())
                , List.of(button)
                , false);
    }
}
