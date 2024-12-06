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

    /**
     * Получить идентификатор пользователя
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * Получить текущее состояние пользователя
     */
    public UserState getState() {
        return state;
    }

    /**
     * Установить текущее состояние пользователя
     */
    public void setState(UserState state) {
        this.state = state;
    }

    /**
     * Получить тест, который в данный момент используется пользователем
     */
    public TestEntity getCurrentTest() {
        return currentTest;
    }

    /**
     * Установить тест, который будет использоваться пользователем
     */
    public void setCurrentTest(TestEntity currentTest) {
        this.currentTest = currentTest;
    }

    /**
     * Получить вопрос, который в данный момент используется пользователем
     */
    public QuestionEntity getCurrentQuestion() {
        return currentQuestion;
    }

    /**
     * Установить вопрос, который будет использоваться пользователем
     */
    public void setCurrentQuestion(QuestionEntity currentQuestion) {
        this.currentQuestion = currentQuestion;
    }

    /**
     * Конструктор без параметров
     */
    public UserSession() {
    }

    /**
     * Конструктор с указанием идентификатора пользователя
     */
    public UserSession(Long userId) {
        this.userId = userId;
    }

    /**
     * Получить индекс редактируемого пользователем варианта ответа
     */
    public Integer getEditingAnswerIndex() {
        return editingAnswerIndex;
    }

    /**
     * Установить индекс редактируемого пользователем варианта ответа
     */
    public void setEditingAnswerIndex(Integer editingAnswerIndex) {
        this.editingAnswerIndex = editingAnswerIndex;
    }
}
