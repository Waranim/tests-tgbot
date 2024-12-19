package org.example.bot.processor;

import org.example.bot.entity.TestEntity;
import org.example.bot.service.TestService;
import org.example.bot.telegram.BotResponse;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Обработчик команды справки.
 */
@Component
public class TestCommandProcessor extends AbstractCommandProcessor {
    /**
     * Сервис для управления тестами.
     */
    private final TestService testService;

    /**
     * Конструктор для инициализации обработчика команды прохождения теста.
     */
    public TestCommandProcessor(TestService testService) {
        super("/test");
        this.testService = testService;
    }

    @Override
    public BotResponse process(Long userId, String message) {

        Optional<List<TestEntity>> testsOpt = testService.getTestsByUserId(userId);
        if(testsOpt.isEmpty())
            return new BotResponse("Тесты не найдены");
        List<TestEntity> tests = testsOpt.get();
        List<String> testsTitles = tests.stream().map(TestEntity::getTitle).toList();
        List<String> testsIds = tests.stream().map(t -> t.getId().toString()).toList();
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        for(int i = 0; i < testsIds.size(); i++){
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(testsTitles.get(i));
            button.setCallbackData("TEST_CHOOSE " + testsIds.get(i));
            buttons.add(button);
        }

        return new BotResponse("Выберите тест: ", buttons, false);
    }
}
