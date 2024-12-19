package org.example.bot.processor;

import org.example.bot.entity.TestEntity;
import org.example.bot.service.TestService;
import org.example.bot.service.UserService;
import org.example.bot.telegram.BotResponse;
import org.example.bot.util.ButtonUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Обработать команду просмотра тестов, полученных от других пользователей
 */
@Component
public class SharedTestsCommandProcessor extends AbstractCommandProcessor {
    private final UserService userService;
    private final ButtonUtils buttonUtils;

    /**
     * Конструктор для инициализации обработчика команды.
     */
    protected SharedTestsCommandProcessor(UserService userService, ButtonUtils buttonUtils) {
        super("/shared_tests");
        this.userService = userService;
        this.buttonUtils = buttonUtils;
    }

    @Override
    public BotResponse process(Long userId, String message) {
        List<TestEntity> receivedTests = userService.getOpenReceivedTests(userId);
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        receivedTests.forEach(t-> buttons.add(buttonUtils.createButton(t.getTitle()
                , "SHARE_UNSUBSCRIBE_CHOOSE_TEST "
                        + t.getId().toString())));
        return new BotResponse("Выберите тест:", buttons, false);
    }
}
