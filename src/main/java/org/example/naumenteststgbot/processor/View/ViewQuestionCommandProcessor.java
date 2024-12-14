package org.example.naumenteststgbot.processor.View;

import org.example.naumenteststgbot.entity.QuestionEntity;
import org.example.naumenteststgbot.entity.TestEntity;
import org.example.naumenteststgbot.processor.AbstractCommandProcessor;
import org.example.naumenteststgbot.service.TestService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Обработчик команды просмотра вопросов
 */
@Component
public class ViewQuestionCommandProcessor extends AbstractCommandProcessor {

    /**
     * Сервис для управления тестами
     */
    private final TestService testService;

    /**
     * Конструктор для инициализации обработчика команды просмотра вопросов
     *
     * @param testService сервис для управления тестами
     */
    public ViewQuestionCommandProcessor(TestService testService) {
        super("/view_question");
        this.testService = testService;
    }

    @Override
    public String process(Long userId, String message) {
        String[] parts = message.split(" ");
        if (parts.length == 1) {
            return "Используйте команду вместе с идентификатором вопроса!";
        }
        if (parts[1].matches("^\\d+$")) {
            Long testId = Long.parseLong(parts[1]);
            TestEntity test = testService.getTest(testId);
            if (test == null || !test.getCreatorId().equals(userId)) {
                return "Тест не найден!";
            }
            List<QuestionEntity> questions = test.getQuestions();
            if (questions.isEmpty()) {
                return "В этом тесте пока нет вопросов.";
            }
            StringBuilder response = new StringBuilder();
            response.append(String.format("Вопросы теста \"%s\":\n", test.getTitle()));
            for (int i = 0; i < questions.size(); i++) {
                QuestionEntity question = questions.get(i);
                response.append(String.format("%d) id:%d  \"%s\"\n", i + 1, question.getId(), question.getQuestion()));
            }
            return response.toString();
        }
        return "Ошибка ввода. Укажите корректный id теста.";
    }
}
