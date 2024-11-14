package org.example.naumenteststgbot.entity;

import jakarta.persistence.*;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

import java.util.Objects;

@Entity
public class AnswerEntity extends BaseEntity {
    @Column(nullable = false)
    private String text; // Текст ответа

    private boolean correct; // Флаг правильности ответа

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnswerEntity that = (AnswerEntity) o;
        return text.equalsIgnoreCase(that.text);  // Сравниваем только текст ответа
    }

    @Override
    public int hashCode() {
        return Objects.hash(text.toLowerCase());  // Используем только текст в качестве хэш-кода
    }


}
