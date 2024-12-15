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
     * Конструктор без параметров
     */
    protected BaseEntity() {
    }

    /**
     * Конструктор с идентификатором для тестов
     */
    protected BaseEntity(Long id) {
        this.id = id;
    }

    /**
     * Получить уникальный идентификатор сущности
     */
    public Long getId() {
        return id;
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

        if (!(o instanceof BaseEntity other)) {
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
        return 17;
    }
}
