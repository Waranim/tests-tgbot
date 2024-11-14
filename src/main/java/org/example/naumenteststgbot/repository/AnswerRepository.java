package org.example.naumenteststgbot.repository;

import org.example.naumenteststgbot.entity.AnswerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для взаимодействия над сущностью ответов в базе данных
 */
@Repository
public interface AnswerRepository extends JpaRepository<AnswerEntity, Long> {
}
