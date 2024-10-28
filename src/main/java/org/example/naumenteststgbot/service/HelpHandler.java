package org.example.naumenteststgbot.service;

import org.springframework.stereotype.Service;

/**
 * Обработчик команд /start и /help
 */
@Service
public class HelpHandler {

    /**
     * Возвращает справочное сообщение
     * @return сообщение пользователю
     */
    public String handle() {
        return "Здравствуйте. Я бот специализирующийся на создании и прохождении тестов. Доступны следующие команды:\n" +
                "/add – Добавить тест\n" +
                "/add_question [testID] - Добавить вопрос к тесту\n" +
                "/test – Посмотреть список тестов начать прохождение\n" +
                "/test [testID] - Посмотреть тест\n" +
                "/question [testID] - Посмотреть список вопросов к тесту\n" +
                "/edit [testID] - Изменить тест с номером testID\n" +
                "/edit_question [questionID] - Изменить вопрос с номером questionID\n" +
                "/del [testID] – Удалить тест с номером testID\n" +
                "/del_question [questionID] - Удалить вопрос с номером questionID\n" +
                "/stop - Закончить ввод вариантов ответа, если добавлено минимум 2 варианта " +
                "при выполнении команды /add_question [testID]\n" +
                "/help - Справка";
    }
}