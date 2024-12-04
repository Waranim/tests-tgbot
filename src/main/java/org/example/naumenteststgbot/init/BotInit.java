package org.example.naumenteststgbot.init;

import org.example.naumenteststgbot.service.TelegramBot;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * Инициализация телеграм бота
 */
@Component
public class BotInit {

    /**
     * Телеграм бот
     */
    private final TelegramBot bot;

    public BotInit(TelegramBot bot) {
        this.bot = bot;
    }

    /**
     * Инициализирует телеграм бота
     */
    @EventListener({ContextRefreshedEvent.class})
    public void init() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Бот не запущен", e);
        }
    }
}
