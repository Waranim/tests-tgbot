package org.example.naumenteststgbot.entity;

import jakarta.persistence.*;

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
}
