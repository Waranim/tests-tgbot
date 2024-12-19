package org.example.bot.util;

import org.example.bot.entity.TestEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Утилитарный класс для тестов
 */
@Component
public class TestUtils {

    /**
     * Получить строковое представление списка тестов
     */
    public String testsToString(List<TestEntity> tests) {
        StringBuilder response = new StringBuilder();
        for(int i = 0; i < tests.size(); i++) {
            TestEntity currentTest = tests.get(i);
            response.append(String.format("%s)  id: %s %s\n", i+1,
                    currentTest.getId(),
                    currentTest.getTitle()));
        }
        return response.toString();
    }
}
