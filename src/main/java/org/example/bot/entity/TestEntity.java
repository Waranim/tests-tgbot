package org.example.bot.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

/**
 * Сущность теста
 */
@Entity
public class TestEntity extends BaseEntity {

    /**
     * Создатель теста
     */
    private Long creatorId;

    /**
     * Вопросы в тесте
     */
    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<QuestionEntity> question = new ArrayList<>();

    /**
     * Название теста
     */
    private String title;

    /**
     * Описание теста
     */
    private String description;

    /**
     * Конструктор без параметров
     */
    public TestEntity() {
    }

    /**
     * Конструктор с указанием идентификатора создателя теста
     */
    public TestEntity(Long creatorId) {
        this.creatorId = creatorId;
    }

    /**
     * Конструктор с указанием идентификатора создателя теста и идентификатором теста
     */
    public TestEntity(Long creatorId, Long testId) {
        super(testId);
        this.creatorId = creatorId;
    }

    /**
     * Получить название теста
     */
    public String getTitle() {
        return title;
    }

    /**
     * Установить название теста
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Установить описание теста
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Получить список вопросов, связанных с тестом
     */
    public List<QuestionEntity> getQuestions() {
        return question;
    }

    /**
     * Получить идентификатор создателя теста.
     */
    public Long getCreatorId() {
        return creatorId;
    }

    /**
     * Получить описание теста
     */
    public String getDescription() {
        return description;
    }
}
