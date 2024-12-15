package org.example.bot.entity;

import jakarta.persistence.*;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

/**
 * Сущность ответов
 */
@Entity
public class AnswerEntity extends BaseEntity {

    /**
     * Формулировка ответа
     */
    @Column(nullable = false)
    private String text;

    /**
     * Флаг правильности ответа
     */
    private boolean correct;

    /**
     * Вопрос, к которому относится данный ответ
     */
    @ManyToOne
    private QuestionEntity question;

    /**
     * Конструктор без параметров
     */
    public AnswerEntity() {
    }

    /**
     * Конструктор с формулировкой ответа
     * По умолчанию ответ считается неправильным
     * @param text строка с текстом ответа
     */
    public AnswerEntity(String text) {
        this.text = text;
        this.correct = false;
    }

    /**
     * Проверить, является ли ответ правильным
     * @return {@code true}, если ответ правильный, иначе {@code false}
     */
    public boolean isCorrect() {
        return correct;
    }

    /**
     * Установить флаг правильности ответа
     * @param correct {@code true}, если ответ правильный, иначе {@code false}
     */
    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    /**
     * Установить вопрос, связанный с ответом
     */
    public void setQuestion(QuestionEntity question) {
        this.question = question;
    }

    /**
     * Получить текст ответа
     */
    public String getAnswerText() {
        return text;
    }

    /**
     * Установить текст ответа
     */
    public void setAnswerText(String answerText) {
        this.text = answerText;
    }
}
