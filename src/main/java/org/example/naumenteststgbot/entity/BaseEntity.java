package org.example.naumenteststgbot.entity;

import jakarta.persistence.*;

/**
 * Базовая сущность Hibernate
 */
@MappedSuperclass
public class BaseEntity {

    /**
     * Уникальный идентификатор сущности
     */
    @Id
    @SequenceGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    protected Long id;

    /**
     * Поле версии
     */
    @Version
    protected Integer version;

    /**
     * Получить уникальный идентификатор сущности
     */
    public Long getId() {
        return id;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof BaseEntity other)) {
            return false;
        }

        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return 17;
    }
}
