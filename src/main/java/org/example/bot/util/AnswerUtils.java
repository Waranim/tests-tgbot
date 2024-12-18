package org.example.bot.util;

import org.example.bot.entity.AnswerEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Утилитарный класс для вопросов
 */
@Component
public class AnswerUtils {

    /**
     * Преобразует список ответов у вопроса в строку
     */
    public String answersToString(List<AnswerEntity> answers) {
        StringBuilder response = new StringBuilder();
        for (int i = 0; i < answers.size(); i++) {
            AnswerEntity answer = answers.get(i);
            response.append(String.format("%d: %s%s\n", i + 1,
                    answer.getAnswerText(),
                    answer.isCorrect() ? " (верный)" : ""));
        }
        return response.toString();
    }
}
