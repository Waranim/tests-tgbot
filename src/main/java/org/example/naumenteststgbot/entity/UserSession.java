package org.example.naumenteststgbot.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;

/**
 * Текущая сессия пользователя
 */
@Entity
public class UserSession extends BaseEntity {
    /**
     * Идентификатор пользователя
     */
    private Long userId;
    /**
     * Состояние пользователя
     */
    private UserState state = UserState.DEFAULT;
    /**
     * Тест, который в данный момент использует пользователь
     */
    @OneToOne
    private TestEntity currentTest;

    /**
     * Индекс редактируемого варианта ответа
     */
    private Integer editingAnswerIndex;
    /**
     * Вопрос, который в данный момент использует пользователь
     */
    @OneToOne(cascade = CascadeType.ALL)
    private QuestionEntity currentQuestion;

    public Long getUserId() {
        return userId;
    }

    public UserState getState() {
        return state;
    }

    public void setState(UserState state) {
        this.state = state;
    }

    public TestEntity getCurrentTest() {
        return currentTest;
    }

    public void setCurrentTest(TestEntity currentTest) {
        this.currentTest = currentTest;
    }

    public QuestionEntity getCurrentQuestion() {
        return currentQuestion;
    }

    public void setCurrentQuestion(QuestionEntity currentQuestion) {
        this.currentQuestion = currentQuestion;
    }

    public UserSession() {
    }

    public UserSession(Long userId) {
        this.userId = userId;
    }

    public Integer getEditingAnswerIndex() {
        return editingAnswerIndex;
    }

    public void setEditingAnswerIndex(Integer editingAnswerIndex) {
        this.editingAnswerIndex = editingAnswerIndex;
    }
}
