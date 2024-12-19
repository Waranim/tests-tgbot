package org.example.bot.processor;

import org.example.bot.entity.TestEntity;
import org.example.bot.service.TestService;
import org.example.bot.telegram.BotResponse;
import org.example.bot.util.ButtonUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Обработать команду поделится
 */
@Component
public class ShareCommandProcessor extends AbstractCommandProcessor {
    private final TestService testService;
    private final ButtonUtils buttonUtils;

    /**
     * Конструктор для инициализации обработчика команды справки.
     */
    public ShareCommandProcessor(TestService testService, ButtonUtils buttonUtils) {
        super("/share");
        this.testService = testService;
        this.buttonUtils = buttonUtils;
    }

    @Override
    public BotResponse process(Long userId, String message) {
        List<TestEntity> tests = testService.getTestsByUserId(userId);
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        tests.forEach(t ->
                buttons.add(buttonUtils.createButton(t.getTitle()
                        , "SHARE_CHOOSE_TEST " + t.getId().toString())));
        return new BotResponse("Выберите тест: ", buttons, false);
    }
}
