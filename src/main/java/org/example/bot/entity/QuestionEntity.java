package org.example.bot.entity;

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
    @OrderBy("id ASC")
    private final List<AnswerEntity> answers;

    /**
     * Тест
     */
    @ManyToOne
    private TestEntity test;

    /**
     * Конструктор без параметров
     */
    public QuestionEntity() {
        answers = new ArrayList<>();
    }

    /**
     * Конструктор с параметром теста
     * @param test тест, связанный с вопросом
     */
    public QuestionEntity(TestEntity test) {
        this.test = test;
        answers = new ArrayList<>();
    }

    /**
     * Конструктор с параметром теста
     *
     * @param test       тест, связанный с вопросом
     * @param questionId идентификатор вопроса
     */
    public QuestionEntity(TestEntity test, Long questionId) {
        super(questionId);
        this.test = test;
        answers = new ArrayList<>();
    }
    /**
     * Получить формулировку вопроса
     */
    public String getQuestion() {
        return question;
    }

    /**
     * Установить формулировку вопроса
     */
    public void setQuestion(String question) {
        this.question = question;
    }

    /**
     * Получить список ответов, связанных с вопросом
     */
    public List<AnswerEntity> getAnswers() {
        return answers;
    }

    /**
     * Получить тест, связанный с вопросом
     */
    public TestEntity getTest() {
        return test;
    }

    /**
     * Установить тест для вопроса
     */
    public void setTest(TestEntity test) {
        this.test = test;
    }
}