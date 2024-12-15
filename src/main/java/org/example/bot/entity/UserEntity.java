package org.example.bot.entity;

import jakarta.persistence.*;

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
     * Сессия пользователя
     */
    @OneToOne(cascade = CascadeType.ALL)
    protected UserSession userSession;

    /**
     * Пустой конструктор
     */
    public UserEntity(){

    }

    /**
     * Конструктор со списком тестов для тестирования
     */
    public UserEntity(List<TestEntity> tests){
        this.tests = tests;
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
     * Установить сессию пользователя
     */
    public void setSession(UserSession userSession) {
        this.userSession = userSession;
    }

    /**
     * Получить сессию пользователя
     */
    public UserSession getSession() {
        return userSession;
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
}
