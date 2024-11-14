package org.example.naumenteststgbot.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

/**
 * Сущность вопроса
 */
@Entity
public class QuestionEntity extends BaseEntity {
    /**
     * Тест, в котором содержится вопрос
     */
    @ManyToOne
    private TestEntity test;
}
