package org.example.bot.repository;

import org.example.bot.entity.TestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для взаимодействия над сущностью тестов в базе данных
 */
@Repository
public interface TestRepository extends JpaRepository<TestEntity, Long> {
}
