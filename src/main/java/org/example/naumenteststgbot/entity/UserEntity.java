package org.example.naumenteststgbot.entity;

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
    private Long id;

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
    @OneToMany(mappedBy = "creatorId")
    private List<TestEntity> tests;

    /**
     * Сессия пользователя
     */
    @OneToOne(cascade = CascadeType.ALL)
    protected UserSession userSession;

    /**
     * Получить уникальный идентификатор пользователя
     */
    public Long getId() {
        return id;
    }

    /**
     * Установить уникальный идентификатор пользователя
     */
    public void setId(Long id) {
        this.id = id;
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

        return id != null && id.equals(other.getId());
    }

    /**
     * Возвращает хэш-код объекта
     * @return целочисленное значение хэш-кода
     */
    @Override
    public int hashCode() {
        return 23;
    }
}
