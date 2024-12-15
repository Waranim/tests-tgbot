package org.example.bot.processor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для обработчика команд /start и /help
 */
class HelpCommandProcessorTest {

    /**
     * Тест на вывод ожидаемого сообщения при вызове команды /start или /help
     */
    @Test
    public void testHandle() {
        HelpCommandProcessor helpCommandProcessor = new HelpCommandProcessor();
        String expectedMessage = """
                Здравствуйте. Я бот специализирующийся на создании и прохождении тестов. Доступны следующие команды:
                /add – Добавить тест
                /add_question [testID] - Добавить вопрос к тесту
                /view – Посмотреть список тестов
                /view [testID] - Посмотреть тест
                /view_question [testID] - Посмотреть список вопросов к тесту
                /edit [testID] - Изменить тест с номером testID
                /edit_question [questionID] - Изменить вопрос с номером questionID
                /del [testID] – Удалить тест с номером testID
                /del_question [questionID] - Удалить вопрос с номером questionID
                /stop - Закончить ввод вариантов ответа, если добавлено минимум 2 варианта \
                при выполнении команды /add_question [testID]
                /help - Справка""";

        String actualMessage = helpCommandProcessor.process(null, null);

        assertEquals(expectedMessage, actualMessage);
    }
}