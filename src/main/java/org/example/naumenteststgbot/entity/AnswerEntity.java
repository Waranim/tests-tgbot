package org.example.naumenteststgbot.entity;

import jakarta.persistence.*;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

import java.util.Objects;

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
     * Вопрос
     */
    @ManyToOne
    private QuestionEntity question;

    public AnswerEntity() {
    }

    public AnswerEntity(String text) {
        this.text = text;
        this.correct = false;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public void setQuestion(QuestionEntity question) {
        this.question = question;
    }

    public String getAnswerText() {
        return text;
    }

    public void setAnswerText(String answerText) {
        this.text = answerText;
    }
}
