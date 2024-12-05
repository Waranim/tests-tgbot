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
    @ManyToMany
    private List<TestEntity> receivedTests;

    @OneToOne(cascade = CascadeType.ALL)
    protected UserSession userSession;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<TestEntity> getTests() {
        return tests;
    }
    public void setSession(UserSession userSession) {
        this.userSession = userSession;
    }
    public UserSession getSession() {
        return userSession;
    }

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
    @Override
    public int hashCode() {
        return 23;
    }

    public List<TestEntity> getReceivedTests() {
        return receivedTests;
    }
}
