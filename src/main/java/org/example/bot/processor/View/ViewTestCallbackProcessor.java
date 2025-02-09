package org.example.bot.processor.View;

import org.example.bot.dto.InlineButtonDTO;
import org.example.bot.entity.TestEntity;
import org.example.bot.processor.AbstractCallbackProcessor;
import org.example.bot.service.TestService;
import org.example.bot.telegram.BotResponse;
import org.example.bot.util.TestUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Обработчик состояния изменения открытия или закрытия теста
 */
@Component
public class ViewTestCallbackProcessor extends AbstractCallbackProcessor {

    /**
     * Сервис для управления тестами
     */
    private final TestService testService;

    /**
     * Утилита с вспомогательными методами для тестов
     */
    private final TestUtils testUtils;

    /**
     * Конструктор для инициализации обработчика измения открытия или закрытия теста
     * @param testService сервис для управления тестами
     * @param testUtils утилита с вспомогательными методами для тестов
     */
    public ViewTestCallbackProcessor(TestService testService, TestUtils testUtils) {
        super("VIEW_TEST");
        this.testService = testService;
        this.testUtils = testUtils;
    }

    @Override
    public BotResponse process(Long userId, String message) {
        String[] parts = message.split(" ");
        if (parts.length != 2) {
            return new BotResponse("Тест не найден");
        }
        Optional<TestEntity> test = testService.getTest(Long.parseLong(parts[1]));
        test.get().setAccessOpen(!test.get().isAccessOpen());
        testService.update(test.get());
        List<List<InlineButtonDTO>> button = new ArrayList<>();
        String buttonsText = test.get().isAccessOpen() ? "Закрыть доступ" : "Открыть доступ";
        button.add(List.of(new InlineButtonDTO(buttonsText, "VIEW_TEST " + test.get().getId())));

        return new BotResponse(testUtils.testToString(test.get()), button, true);
    }
}
