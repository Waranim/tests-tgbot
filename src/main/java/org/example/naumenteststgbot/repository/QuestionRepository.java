package org.example.naumenteststgbot.repository;


import org.example.naumenteststgbot.entity.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
/**
 * Репозиторий для взаимодействия над сущностью вопросов в базе данных
 */
@Repository
public interface QuestionRepository extends JpaRepository<QuestionEntity, Long> {
}
