package org.example.naumenteststgbot.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
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
    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, orphanRemoval = true)
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
     * Открытие-закрытие доступа к тесту
     */
    private boolean accessOpen;

    public TestEntity() {
    }
    public TestEntity(Long creatorId) {
        this.creatorId = creatorId;
        this.accessOpen = false;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }



    public void setDescription(String description) {
        this.description = description;
    }

    public List<QuestionEntity> getQuestions() {
        return question;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    /**
     * Вернуть значение состояния доступа
     */
    public boolean isAccessOpen() {
        return accessOpen;
    }

    /**
     * Установить значение состояния доступа
     */
    public void setAccessOpen(boolean accessOpen) {
        this.accessOpen = accessOpen;
    }
}
