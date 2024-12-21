package org.example.bot.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Сущность пользователя
 */
@Entity
@Table(name = "users")
public class UserEntity {

    /**
     * Идентификатор пользователя в телеграм
     */
    @Id
    private Long userId;

    /**
     * Поле версии
     */
    @Version
    private Integer version;

    /**
     * Псевдоним пользователя
     */
    @Column(unique = true, nullable = false)
    private String username;

    /**
     * Список тестов пользователя
     */
    @OneToMany(mappedBy = "creatorId", fetch = FetchType.EAGER)
    private List<TestEntity> tests;

    /**
     * Список полученных тестов от других пользователей
     */
    @ManyToMany(fetch = FetchType.EAGER)
    private final List<TestEntity> receivedTests;

    /**
     * Контекст пользователя
     */
    @OneToOne(cascade = CascadeType.ALL)
    protected UserContext userContext;

    /**
     * Пустой конструктор
     */
    public UserEntity(){
        tests = new ArrayList<>();
        receivedTests = new ArrayList<>();
    }

    /**
     * Конструктор с параметрами
     *
     * @param userId идентификатор пользователя
     * @param username псевдоним пользователя
     * @param userContext контекст пользователя
     */
    public UserEntity(Long userId, String username, UserContext userContext) {
        this.userId = userId;
        this.username = username;
        this.userContext = userContext;
        tests = new ArrayList<>();
        receivedTests = new ArrayList<>();
    }

    /**
     * Конструктор со списком тестов для тестирования
     */
    public UserEntity(List<TestEntity> tests){
        this.tests = tests;
        receivedTests = new ArrayList<>();
    }

    /**
     * Получить уникальный идентификатор пользователя
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * Установить уникальный идентификатор пользователя
     */
    public void setUserId(Long id) {
        this.userId = id;
    }

    /**
     * Получить псевдоним пользователя
     */
    public String getUsername() {
        return username;
    }

    /**
     * Установить псевдоним пользователя
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Получить список тестов, созданных пользователем
     */
    public List<TestEntity> getTests() {
        return tests;
    }

    /**
     * Установить контекст пользователя
     */
    public void setContext(UserContext userContext) {
        this.userContext = userContext;
    }

    /**
     * Получить контекст пользователя
     */
    public UserContext getContext() {
        return userContext;
    }

    /**
     * Сравнивает текущий объект с другим объектом
     * @param o объект для сравнения
     * @return true если объекты равны, false в противном случае
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof UserEntity other)) {
            return false;
        }

        return userId != null && userId.equals(other.getUserId());
    }

    /**
     * Возвращает хэш-код объекта
     * @return целочисленное значение хэш-кода
     */
    @Override
    public int hashCode() {
        return Long.hashCode(userId);
    }

    /**
     * Получить полученные тесты пользователя
     */
    public List<TestEntity> getReceivedTests() {
        return receivedTests;
    }
}
