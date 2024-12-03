package org.example.naumenteststgbot.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность вопроса
 */
@Entity
public class QuestionEntity extends BaseEntity {
    /**
     * Формулировка вопроса
     */
    private String question;

    /**
     * Ответы в вопросе
     */
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<AnswerEntity> answers = new ArrayList<>();

    /**
     * Тест
     */
    @ManyToOne
    private TestEntity test;

    public QuestionEntity() {
    }

    public QuestionEntity(TestEntity test) {
        this.test = test;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<AnswerEntity> getAnswers() {
        return answers;
    }

    public TestEntity getTest() {
        return test;
    }

    public void setTest(TestEntity test) {
        this.test = test;
    }
}