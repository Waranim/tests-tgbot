package org.example.bot.processor.share;

import org.example.bot.dto.InlineButtonDTO;
import org.example.bot.entity.TestEntity;
import org.example.bot.processor.AbstractCommandProcessor;
import org.example.bot.service.TestService;
import org.example.bot.telegram.BotResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Обработать команду поделится
 */
@Component
public class ShareCommandProcessor extends AbstractCommandProcessor {
    private final TestService testService;

    /**
     * Конструктор для инициализации обработчика команды справки.
     */
    public ShareCommandProcessor(TestService testService) {
        super("/share");
        this.testService = testService;
    }

    @Override
    public BotResponse process(Long userId, String message) {
        Optional<List<TestEntity>> tests = testService.getTestsByUserId(userId);
        List<List<InlineButtonDTO>> buttons = new ArrayList<>();
        if (tests.isEmpty())
            return new BotResponse("Тесты не найдены!");

        tests.get().forEach(t ->
                buttons.add(List.of(new InlineButtonDTO(t.getTitle(),
                        "SHARE_CHOOSE_TEST " + t.getId().toString()))));
        return new BotResponse("Выберите тест: ", buttons, false);
    }
}
