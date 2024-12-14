package org.example.naumenteststgbot.processor;

import org.example.naumenteststgbot.service.UserService;
import org.springframework.stereotype.Component;

/**
 * Обработчик команды /start.
 */
@Component
public class StartCommandProcessor extends AbstractCommandProcessor {
    /**
     * Сервис для управления пользователями.
     */
    private final UserService userService;

    /**
     * Обработчик команды справки.
     */
    private final HelpCommandProcessor helpCommandProcessor;

    /**
     * Конструктор для инициализации обработчика команды /start.
     * 
     * @param userService сервис для управления пользователями
     * @param helpCommandProcessor обработчик команды справки
     */
    public StartCommandProcessor(UserService userService, HelpCommandProcessor helpCommandProcessor) {
        super("/start");
        this.userService = userService;
        this.helpCommandProcessor = helpCommandProcessor;
    }

    @Override
    public String process(Long userId, String message) {
        userService.create(userId, message.split(" ")[1]);
        return helpCommandProcessor.process(userId, message);
    }
}

