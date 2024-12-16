package org.example.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Приложение телеграм-бота для создания и прохождения тестов
 */
@SpringBootApplication
public class TestsTgbotApplication {

    /**
     * Точка входа в приложение
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        SpringApplication.run(TestsTgbotApplication.class, args);
    }

}
