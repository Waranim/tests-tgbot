package org.example.bot.util;

import org.example.bot.entity.AnswerEntity;
import org.example.bot.entity.QuestionEntity;
import org.example.bot.entity.TestEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Util {
    /**
     * Узнать, находится ли в строке только лишь число
     * @return true - если только цифры в строке, false - все остальные случаи.
     */
    public boolean isNumber(String number) {
        return number.matches("^-?\\d+$");
    }

    /**
     * Получить развернутое строковое представление сущности теста
     */
    public String testToString(TestEntity test) {
        List<QuestionEntity> questions = test.getQuestions();
        StringBuilder response = new StringBuilder(String.format("Тест “%s”. Всего вопросов: %s\n",  test.getTitle(), questions.size()));
        for (QuestionEntity question : questions) {
            response.append("Вопрос: %s\nВарианты ответов:\n".formatted(question.getQuestion()));
            List<AnswerEntity> answers = question.getAnswers();
            AnswerEntity correctAnswer = null;
            for (int i = 0; i < answers.size(); i++) {
                var answer = answers.get(i);
                response.append("%s - %s\n".formatted(i+1, answer.getAnswerText()));
                if(answer.isCorrect()) correctAnswer = answer;
            }
            response.append("Правильный вариант: ").append(correctAnswer.getAnswerText()).append("\n\n");
        }
        return response.toString();
    }

    /**
     * Получить строковое представление списка тестов
     */
    public String testsListToString(List<TestEntity> tests) {
        StringBuilder response = new StringBuilder();
        for(int i = 0; i < tests.size(); i++) {
            TestEntity currentTest = tests.get(i);
            response.append(String.format("%s)  id: %s %s\n", i+1, currentTest.getId(), currentTest.getTitle()));
        }
        return response.toString();
    }
}
