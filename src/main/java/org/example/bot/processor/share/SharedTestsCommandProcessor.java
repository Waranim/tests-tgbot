package org.example.bot.processor.share;

import org.example.bot.dto.InlineButtonDTO;
import org.example.bot.entity.TestEntity;
import org.example.bot.entity.UserEntity;
import org.example.bot.processor.AbstractCommandProcessor;
import org.example.bot.service.UserService;
import org.example.bot.telegram.BotResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Обработать команду просмотра тестов, полученных от других пользователей
 */
@Component
public class SharedTestsCommandProcessor extends AbstractCommandProcessor {
    private final UserService userService;

    /**
     * Конструктор для инициализации обработчика команды.
     */
    public SharedTestsCommandProcessor(UserService userService) {
        super("/shared_tests");
        this.userService = userService;
    }

    @Override
    public BotResponse process(Long userId, String message) {
        List<TestEntity> receivedTests = userService.getOpenReceivedTests(userId);
        List<List<InlineButtonDTO>> buttons = new ArrayList<>();
        receivedTests.forEach(t->{
            String creatorUsername = userService.getUserById(t.getCreatorId())
                    .map(UserEntity::getUsername)
                    .orElse("неизвестный пользователь");

                buttons.add(List.of(new InlineButtonDTO("%s (%s)".formatted(t.getTitle(), creatorUsername),
                "SHARE_UNSUBSCRIBE_CHOOSE_TEST "
                + t.getId().toString())));});
        return new BotResponse("Выберите тест:", buttons, false);
    }
}
