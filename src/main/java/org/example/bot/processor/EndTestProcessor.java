package org.example.bot.processor;

import org.example.bot.telegram.BotResponse;
import org.springframework.stereotype.Component;

/**
 * Обработка ответа на вопрос
 */
@Component
public class EndTestProcessor extends AbstractCallbackProcessor {

    /**
     * Конструктор для инициализации обработчика callback.
     */
    protected EndTestProcessor() {
        super("END_TEST");
    }

    @Override
    public BotResponse process(Long userId, String callback) {
        return new BotResponse("Тест завершен");
    }
}
