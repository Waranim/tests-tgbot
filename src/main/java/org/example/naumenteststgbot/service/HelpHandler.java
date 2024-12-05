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
        return """
                Здравствуйте. Я бот специализирующийся на создании и прохождении тестов. Доступны следующие команды:
                /add – Добавить тест
                /add_question [testID] - Добавить вопрос к тесту
                /view – Посмотреть список тестов
                /view [testID] - Посмотреть тест
                /test - Начать прохождение теста
                /view_question [testID] - Посмотреть список вопросов к тесту
                /edit [testID] - Изменить тест с номером testID
                /edit_question [questionID] - Изменить вопрос с номером questionID
                /del [testID] – Удалить тест с номером testID
                /del_question [questionID] - Удалить вопрос с номером questionID
                /stop - Закончить ввод вариантов ответа, если добавлено минимум 2 варианта \
                при выполнении команды /add_question [testID]
                /help - Справка""";
    }
}