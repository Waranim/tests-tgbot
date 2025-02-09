package org.example.bot.entity;

import jakarta.persistence.*;

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
    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private final List<QuestionEntity> question;

    /**
     * Пользователи, которые получили доступ к данному тесту
     */
    @ManyToMany(fetch = FetchType.EAGER)
    private final List<UserEntity> recipients;

    /**
     * Название теста
     */
    private String title;

    /**
     * Описание теста
     */
    private String description;

    /**
     * Количество попыток прохождения
     */
    private Integer countTries;

    /**
     * Количество правильных ответов всех пользователей
     */
    private Integer correctAnswerCountAllUsers;

    /**
     * Количество решённых вопросов всех пользователей
     */
    private Integer countAnsweredQuestionsAllUsers;

    /**
     * Открыт ли тест
     */
    private boolean isAccessOpen;

    /**
     * Конструктор без параметров
     */
    public TestEntity() {
        recipients = new ArrayList<>();
        question = new ArrayList<>();
        isAccessOpen = true;
    }

    /**
     * Конструктор с указанием идентификатора создателя теста
     */
    public TestEntity(Long creatorId) {
        this.creatorId = creatorId;
        question = new ArrayList<>();
        recipients = new ArrayList<>();
        isAccessOpen = true;
        this.countTries = 0;
        this.correctAnswerCountAllUsers = 0;
        this.countAnsweredQuestionsAllUsers = 0;
    }

    /**
     * Конструктор с указанием идентификатора создателя теста и идентификатором теста
     */
    public TestEntity(Long creatorId, Long testId) {
        super(testId);
        this.creatorId = creatorId;
        question = new ArrayList<>();
        recipients = new ArrayList<>();
        isAccessOpen = true;
        this.countTries = 0;
        this.correctAnswerCountAllUsers = 0;
        this.countAnsweredQuestionsAllUsers = 0;
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
     * Получить количество попыток прохождения
     */
    public Integer getCountTries() {
        return countTries;
    }

    /**
     * Установить количество попыток прохождения
     */
    public void setCountTries(Integer countTries) {
        this.countTries = countTries;
    }

    /**
     * Получить количество правильных ответов всех пользователей
     */
    public Integer getCorrectAnswerCountAllUsers() {
        return correctAnswerCountAllUsers;
    }

    /**
     * Установить количество правильных ответов всех пользователей
     */
    public void setCorrectAnswerCountAllUsers(Integer correctAnswerCountAllUsers) {
        this.correctAnswerCountAllUsers = correctAnswerCountAllUsers;
    }

    /**
     * Получить количество решённых вопросов всех пользователей
     */
    public Integer getCountAnsweredQuestionsAllUsers() {
        return countAnsweredQuestionsAllUsers;
    }

    /**
     * Установить количество решённых вопросов всех пользователей
     */
    public void setCountAnsweredQuestionsAllUsers(Integer countAnsweredQuestionsAllUsers) {
        this.countAnsweredQuestionsAllUsers = countAnsweredQuestionsAllUsers;
    }

    /**
     * Получить описание теста
     */
    public String getDescription() {
        return description;
    }

    /**
     * Получить пользователей с доступом к тесту
     */
    public List<UserEntity> getRecipients() {
        return recipients;
    }

    /**
     * Проверить открыт ли у теста доступ
     */
    public boolean isAccessOpen() {
        return isAccessOpen;
    }

    /**
     * Установить доступ к тесту
     */
    public void setAccessOpen(boolean isAccessOpen) {
        this.isAccessOpen = isAccessOpen;
    }
}
