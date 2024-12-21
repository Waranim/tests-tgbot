package org.example.bot.processor.View;

import org.example.bot.dto.InlineButtonDTO;
import org.example.bot.entity.TestEntity;
import org.example.bot.processor.AbstractCommandProcessor;
import org.example.bot.service.StateService;
import org.example.bot.service.TestService;
import org.example.bot.state.UserState;
import org.example.bot.telegram.BotResponse;
import org.example.bot.util.NumberUtils;
import org.example.bot.util.TestUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Обработчик команды просмотра тестов.
 */
@Component
public class ViewCommandProcessor extends AbstractCommandProcessor {
    /**
     * Сервис для управления тестами.
     */
    private final TestService testService;
    
    /**
     * Сервис для управления состояниями.
     */
    private final StateService stateService;
    
    /**
     * Утилита с вспомогательными числовыми методами.
     */
    private final NumberUtils numberUtils;

    /**
     * Утилита с вспомогательными методами для тестов.
     */
    private final TestUtils testUtils;

    /**
     * Конструктор для инициализации обработчика команды просмотра тестов.
     * 
     * @param testService сервис для управления тестами
     * @param stateService сервис для управления состояниями
     * @param numberUtils утилита с вспомогательными числовыми методами
     * @param testUtils утилита с вспомогательными методами для тестов
     */
    public ViewCommandProcessor(TestService testService,
                                StateService stateService,
                                NumberUtils numberUtils,
                                TestUtils testUtils) {
        super("/view");
        this.testService = testService;
        this.stateService = stateService;
        this.numberUtils = numberUtils;
        this.testUtils = testUtils;
    }

    @Override
    public BotResponse process(Long userId, String message) {
        String[] parts = message.split(" ");
        Optional<List<TestEntity>> testsOptional = testService.getTestsByUserId(userId);

        if (parts.length == 1) {
            stateService.changeStateById(userId, UserState.VIEW_TEST);
            String text = testsOptional.isPresent()? testUtils.testsToString(testsOptional.get()) : "";
            return new BotResponse("Выберите тест для просмотра:\n"
                    + text);
        } else if (numberUtils.isNumber(parts[1])){
            stateService.changeStateById(userId, UserState.DEFAULT);
            Long testId = Long.parseLong(parts[1]);
            Optional<TestEntity> test = testService.getTest(testId);
            if (test.isEmpty() || testsOptional.isEmpty() || !testsOptional.get().contains(test.get()))
                return new BotResponse("Тест не найден!");
            List<List<InlineButtonDTO>> button = new ArrayList<>();
            String buttonsText = test.get().isAccessOpen() ? "Закрыть доступ" : "Открыть доступ";
            button.add(List.of(new InlineButtonDTO(buttonsText, "VIEW_TEST " + testId)));

            return new BotResponse(testUtils.testToString(test.get()), button, false);
        }
        return new BotResponse("Ошибка ввода!");
    }
}
