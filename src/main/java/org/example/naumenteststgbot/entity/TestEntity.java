package org.example.naumenteststgbot.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

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
    @OneToMany(mappedBy = "test")
    private List<QuestionEntity> questions;

    /**
     * Название теста
     */
    private String title;

    /**
     * Описание теста
     */
    private String description;

    public TestEntity() {
    }
    public TestEntity(Long creatorId) {
        this.creatorId = creatorId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<QuestionEntity> getQuestions() {
        return questions;
    }
}
