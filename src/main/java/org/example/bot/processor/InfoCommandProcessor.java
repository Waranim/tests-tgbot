package org.example.bot.processor;

import org.example.bot.telegram.BotResponse;
import org.springframework.stereotype.Component;

/**
 * Обработчик команды получения идентификатора пользователя
 */
@Component
public class InfoCommandProcessor extends AbstractCommandProcessor {

    /**
     * Конструктор для инициализации обработчика команды получения идентификатора пользователя
     */
    public InfoCommandProcessor() {
        super("/info");
    }

    @Override
    public BotResponse process(Long userId, String message) {
        return new BotResponse(String.format("Ваш идентификатор: %s", userId));
    }
}
