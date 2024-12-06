package org.example.naumenteststgbot.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация телеграм бота
 */
@Configuration
public class BotConfig {

    /**
     * Имя бота
     */
    @Value("${bot.name}")
    private String name;

    /**
     * Токен бота
     */
    @Value("${bot.token}")
    private String token;

    public BotConfig() {
    }

    /**
     * Получить имя бота
     */
    public String getName() {
        return name;
    }

    /**
     * Получить токен бота
     */
    public String getToken() {
        return token;
    }
}
